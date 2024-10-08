package com.daewon.xeno_backend.service;

import com.daewon.xeno_backend.domain.Products;
import com.daewon.xeno_backend.domain.Review;
import com.daewon.xeno_backend.domain.auth.*;
import com.daewon.xeno_backend.dto.auth.*;
import com.daewon.xeno_backend.dto.user.UserUpdateDTO;
import com.daewon.xeno_backend.exception.UnauthorizedException;
import com.daewon.xeno_backend.exception.UserNotFoundException;
import com.daewon.xeno_backend.repository.Products.*;
import com.daewon.xeno_backend.repository.RefreshTokenRepository;
import com.daewon.xeno_backend.repository.ReplyRepository;
import com.daewon.xeno_backend.repository.ReviewRepository;
import com.daewon.xeno_backend.repository.auth.*;
import com.daewon.xeno_backend.utils.JWTUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final BrandRepository brandRepository;
    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ProductsRepository productsRepository;
    private final ProductsOptionRepository productsOptionRepository;
    private final ProductsImageRepository productsImageRepository;
    private final ProductsStarRepository productsStarRepository;
    private final ProductsLikeRepository productsLikeRepository;

    private final ProductsBrandRepository productsBrandRepository;
    private final ReviewRepository reviewRepository;
    private final ReplyRepository replyRepository;

    private final BrandApprovalRepository brandApprovalRepository;


    @Override
    @Transactional
    public Users signup(UserSignupDTO userSignupDTO) throws UserEmailExistException {
        if(userRepository.existsByEmail(userSignupDTO.getEmail())) {
            throw new UserEmailExistException();
        }

        // Customer 엔티티를 먼저 생성.
        Customer customer = Customer.builder()
                .point(1000)
                .level(Level.BRONZE)
                .build();

        // Users 엔티티를 생성하고 저장.
        Users user = Users.builder()
                .email(userSignupDTO.getEmail())
                .password(passwordEncoder.encode(userSignupDTO.getPassword()))
                .name(userSignupDTO.getName())
                .address(userSignupDTO.getAddress())
                .phoneNumber(userSignupDTO.getPhoneNumber())
                .customer(customer)
                .build();

        user.addRole(UserRole.USER);
        user = userRepository.save(user);

        // Customer에 Users엔티티를 생성할때 할당받은 userId를 설정하고 저장.
        customer.setUserId(user.getUserId());
        customerRepository.save(customer);

        return user;
    }

    // 판매사 회원가입시 승인대기 상태로 돌리는 메서드
    @Override
    public BrandApprovalDTO requestBrandSignup(BrandDTO brandDTO) {
        // 먼저 동일한 brandName과 email로 승인 대기 중인 신청이 있는지 확인
        Optional<BrandApproval> existingApproval = brandApprovalRepository.findByBrandNameAndEmailAndStatus(
                brandDTO.getBrandName(), brandDTO.getEmail(), "승인 대기");

        if (existingApproval.isPresent()) {
            throw new IllegalArgumentException("이 브랜드 이름과 이메일로 이미 승인 대기 중인 신청이 있습니다.");
        }

        // 새로운 BrandApproval 생성
        BrandApproval brandApproval = BrandApproval.builder()
                .brandName(brandDTO.getBrandName())
                .companyId(brandDTO.getCompanyId())
                .email(brandDTO.getEmail())
                .password(passwordEncoder.encode(brandDTO.getPassword()))
                .name(brandDTO.getName())
                .address(brandDTO.getAddress())
                .phoneNumber(brandDTO.getPhoneNumber())
                .status("승인 대기")
                .build();
        brandApproval.addRole(UserRole.SELLER);

        BrandApproval savedBrandApproval = brandApprovalRepository.save(brandApproval);
        return new BrandApprovalDTO(savedBrandApproval.getId(), savedBrandApproval.getEmail(), savedBrandApproval.getBrandName(), "승인 대기");
    }

    @Override
    public UserSignupDTO signupBrand(String managerEmail, Long approvalId) {
        // 매니저 검증
        Users manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new UserNotFoundException("매니저를 찾을 수 없습니다."));

        if (!manager.getRoleSet().contains(UserRole.MANAGER)) {
            throw new UnauthorizedException("브랜드 승인 권한이 없습니다.");
        }

        BrandApproval brandApproval = brandApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("승인이 완료되지 않았습니다."));

        if (!"승인 대기".equals(brandApproval.getStatus())) {
            throw new IllegalStateException("승인 대기 상태입니다.");
        }

        // 기존 브랜드 찾기 또는 새로 생성
        Brand brand = brandRepository.findByBrandName(brandApproval.getBrandName())
                .orElseGet(() -> {
                    Brand newBrand = Brand.builder()
                            .brandName(brandApproval.getBrandName())
                            .companyId(brandApproval.getCompanyId())
                            .build();
                    newBrand.addRole(UserRole.SELLER);
                    return brandRepository.save(newBrand);
                });

        // User 생성 및 저장
        Users user = Users.builder()
                .email(brandApproval.getEmail())
                .password(brandApproval.getPassword())
                .name(brandApproval.getName())
                .address(brandApproval.getAddress())
                .phoneNumber(brandApproval.getPhoneNumber())
                .brand(brand)
                .build();
        user.addRole(UserRole.SELLER);
        Users savedUser = userRepository.save(user);

        brandApproval.setStatus("승인 완료");
        brandApprovalRepository.save(brandApproval);

        UserSignupDTO signupDTO = UserSignupDTO.builder()
                .userId(savedUser.getUserId())
                .email(savedUser.getEmail())
                .password(savedUser.getPassword())
                .name(savedUser.getName())
                .address(savedUser.getAddress())
                .phoneNumber(savedUser.getPhoneNumber())
                .brand(BrandSignupDTO.builder()
                        .brandName(brand.getBrandName())
                        .companyId(brand.getCompanyId())
                        .build())
                .build();

        return signupDTO;
    }

    // 관리자 회원가입
    @Override
    public Manager signupManager(UserSignupDTO userSignupDTO) {
        Manager manager = Manager.builder()
                .build();

        Users user = Users.builder()
                .email(userSignupDTO.getEmail())
                .password(passwordEncoder.encode(userSignupDTO.getPassword()))
                .name(userSignupDTO.getName())
                .address(userSignupDTO.getAddress())
                .phoneNumber(userSignupDTO.getPhoneNumber())
                .manager(manager)
                .build();

        user.addRole(UserRole.MANAGER);
        user = userRepository.save(user);

        // Customer에 Users엔티티를 생성할때 할당받은 userId를 설정하고 저장.
        manager.setUserId(user.getUserId());
        managerRepository.save(manager);

        return manager;
    }

    @Override
    public Users signin(String email, String password) {
        // log.info("클라이언트 이메일 : " + email);

        Optional<Users> optionalUsers = userRepository.findByEmail(email);
        // 클라이언트한테 받은 password값과 db에 있는 password값 대조
        // DB에 저장되어 있는 password값이 인코딩 되어서 저장되어 있으므로
        // 클라이언트한테 받은 password값을 인코딩 시켜 대조 시켜야 함
        // 앞에 password는 클라이언트한테 받은 패스워드, 뒤에는 db에 있는 패스워드 값
        if (optionalUsers.isPresent()) {
            if (passwordEncoder.matches(password, optionalUsers.get().getPassword()) && email.equals(optionalUsers.get().getEmail())) {
                log.info("클라이언트 이메일 :" + email);
                log.info("클라이언트 패스워드 : " + password);
                log.info("login success");
                return optionalUsers.get();
            } else {
                log.info("클라이언트 패스워드 : " + password);
                log.info("login fail");
                log.info("클라이언트가 입력한 패스워드" + password +"을 DB에서 찾지 못했습니다.");
                return null;
            }
        } else {
            log.info("클라이언트 이메일 :" + email);
            log.info("login fail");
            log.info("클라이언트가 입력한 이메일" + email + "을 DB에서 찾지 못했습니다");
            return null;
        }
    }

    // user정보 password, name, address, phoneNumber 수정
    @Transactional
    @Override
    public Users updateUser(String email, UserUpdateDTO updateDTO) throws UserNotFoundException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User email을 찾을 수 없음 : " + email));

        // password 업데이트
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        // name 업데이트
        if (updateDTO.getName() != null && !updateDTO.getName().isEmpty()) {
            user.setName(updateDTO.getName());
        }

        // address 업데이트
        if (updateDTO.getAddress() != null && !updateDTO.getAddress().isEmpty()) {
            user.setAddress(updateDTO.getAddress());
        }

        // phoneNumber 업데이트
        if (updateDTO.getPhoneNumber() != null && !updateDTO.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(updateDTO.getPhoneNumber());
        }

        return userRepository.save(user);
    }

    // user탈퇴 메서드
    @Transactional
    @Override
    public void deleteUser(String email) throws UserNotFoundException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User의 email을 찾을수 없습니다 : " + email));

        // Customer 엔티티 삭제
        if (user.getCustomer() != null) {
            customerRepository.delete(user.getCustomer());
        }

        // Users 엔티티 삭제
        userRepository.delete(user);
    }

    // brand 탈퇴 메서드
    @Transactional
    @Override
    public void deleteBrand(String email) throws UserNotFoundException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User의 email을 찾을 수 없습니다 : " + email));

        Brand brand = user.getBrand();
        if (brand == null) {
            throw new IllegalStateException("해당 사용자는 Brand 계정이 아닙니다.");
        }

        String brandName = brand.getBrandName();

        // product에 엮여있는 엔티티 값들 삭제 ... review, reply등
        List<Products> products = productsRepository.findByBrandName(brandName);
        for (Products product: products) {
            deleteProductData(product);
        }

        // Brand 엔티티 삭제
        brandRepository.delete(brand);
        log.info("브랜드 {} 가 삭제되었습니다.", brand.getBrandName());

        // User 엔티티 삭제
        userRepository.delete(user);
        log.info("사용자 계정 (email: {})이 삭제되었습니다.", email);

        log.info("브랜드 계정 및 관련 데이터가 성공적으로 삭제되었습니다. Email: {}", email);
    }

    @Override
    public BrandInfoCardDTO readBrandInfo(UserDetails userDetails) {
        Users users = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        BrandInfoCardDTO dto = new BrandInfoCardDTO();
                dto.setBrandName(users.getBrand().getBrandName());
                dto.setName(users.getName());

        return dto;
    }

    @Override
    public TokenDTO tokenReissue(String refreshToken) {

        Map<String, Object> claims = jwtUtil.validateToken(refreshToken);

        String email = (String) claims.get("email");

        String newAccessToken = jwtUtil.generateToken(Map.of("email", email), 1); // 30분 유효

        return new TokenDTO(newAccessToken, refreshToken);
    }

    private UserSignupDTO convertToDTO(Users user) {
        UserSignupDTO dto = new UserSignupDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());

        if (user.getBrand() != null) {
            BrandSignupDTO brandDTO = new BrandSignupDTO();
            brandDTO.setBrandId(user.getBrand().getBrandId());
            brandDTO.setBrandName(user.getBrand().getBrandName());
            brandDTO.setCompanyId(user.getBrand().getCompanyId());
        }

        return dto;
    }

    // 현재 삭제하려는 product에 관련된 데이터를 지우는 메서드
    @Transactional
    protected void deleteProductData(Products product) {
        log.info("누구임? " + product.getProductId());
        int deletedOptions = productsOptionRepository.deleteAllByProductId(product.getProductId());
        log.info("제품 ID: {}에 대한 {} 개의 옵션이 삭제되었습니다.", product.getProductId(), deletedOptions);
        productsImageRepository.deleteByProducts(product);
        productsStarRepository.deleteByProducts(product);
        productsLikeRepository.deleteByProducts(product);
        productsBrandRepository.deleteByProducts(product);

        productsRepository.delete(product);

        log.info("제품 ID: {} 및 관련 데이터가 삭제되었습니다.", product.getProductId());

    }
}
