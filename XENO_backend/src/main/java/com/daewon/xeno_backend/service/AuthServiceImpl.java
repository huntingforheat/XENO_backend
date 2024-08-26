package com.daewon.xeno_backend.service;

import com.daewon.xeno_backend.domain.auth.Brand;
import com.daewon.xeno_backend.domain.auth.Level;
import com.daewon.xeno_backend.domain.auth.UserRole;
import com.daewon.xeno_backend.domain.auth.Users;
import com.daewon.xeno_backend.dto.auth.AuthSignupDTO;
import com.daewon.xeno_backend.dto.auth.BrandDTO;
import com.daewon.xeno_backend.dto.auth.SellerInfoCardDTO;
import com.daewon.xeno_backend.dto.auth.TokenDTO;
import com.daewon.xeno_backend.dto.signup.BrandRegisterDTO;
import com.daewon.xeno_backend.dto.signup.UserRegisterDTO;
import com.daewon.xeno_backend.repository.BrandRepository;
import com.daewon.xeno_backend.repository.RefreshTokenRepository;
import com.daewon.xeno_backend.repository.UserRepository;
import com.daewon.xeno_backend.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;
    private final BrandRepository brandRepository;

    @Override
    public Users signup(AuthSignupDTO authSignupDTO) throws UserEmailExistException {
        if(userRepository.existsByEmail(authSignupDTO.getEmail())) {
            throw new UserEmailExistException();
        }

        Users users = modelMapper.map(authSignupDTO, Users.class);

        users.setPassword(passwordEncoder.encode(authSignupDTO.getPassword()));
        users.addRole(UserRole.USER);
        users.addLevel(Level.BRONZE);

        log.info("================================");
        log.info(users);
        log.info(users.getRoleSet());

        userRepository.save(users);
        return users;
    }

    @Override
    public UserRegisterDTO registerBrandUser(BrandDTO dto) {
        Brand brand = brandRepository.findByBrandName(dto.getBrandName())
                .orElseGet(() -> {
                    if (dto.getCompanyId() == null) {
                        throw new IllegalArgumentException("New brand requires a company ID");
                    }
                    Brand newBrand = Brand.builder()
                            .brandName(dto.getBrandName())
                            .companyId(dto.getCompanyId())
                            .build();
                    newBrand.addRole(UserRole.SELLER);
                    return brandRepository.save(newBrand);
                });

        Users user = Users.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .brand(brand)
                .build();
        user.addRole(UserRole.SELLER);

        Users savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    // 판매자 회원가입 추가
    @Override
    public Users signupSeller(AuthSignupDTO authSignupDTO) throws UserEmailExistException {
        if(userRepository.existsByEmail(authSignupDTO.getEmail())) {
            throw new UserEmailExistException();
        }
    
        Users users = modelMapper.map(authSignupDTO, Users.class);

        users.setName(authSignupDTO.getName());  // 명시적으로 이름 설정
        users.setPassword(passwordEncoder.encode(authSignupDTO.getPassword()));
        users.addRole(UserRole.SELLER);
//        users.setCompanyId(authSignupDTO.getCompanyId());
//        users.setBrandName(authSignupDTO.getBrandName());
        users.setAddress(authSignupDTO.getAddress());
        users.setPhoneNumber(authSignupDTO.getPhoneNumber());
    
        log.info("================================");
        log.info(users);
        log.info(users.getRoleSet());
    
        userRepository.save(users);
        return users;
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

    @Override
    public SellerInfoCardDTO readSellerInfo(UserDetails userDetails) {
        Users users = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        SellerInfoCardDTO dto = new SellerInfoCardDTO();
//                dto.setBrandName(users.getBrandName());
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

    private UserRegisterDTO convertToDTO(Users user) {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoleSet(user.getRoleSet().stream().map(Enum::name).collect(Collectors.toSet()));

        if (user.getBrand() != null) {
            BrandRegisterDTO brandDTO = new BrandRegisterDTO();
            brandDTO.setBrandId(user.getBrand().getBrandId());
            brandDTO.setBrandName(user.getBrand().getBrandName());
            brandDTO.setCompanyId(user.getBrand().getCompanyId());
            brandDTO.setRoleSet(user.getBrand().getRoleSet().stream().map(Enum::name).collect(Collectors.toSet()));
            dto.setBrand(brandDTO);
        }

        return dto;
    }
}
