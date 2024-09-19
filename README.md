XENO Backend
📖 설명
남자 패션 쇼핑몰 프로젝트 XENO입니다.

🧑‍💻 팀원 소개

박정태

채영준

강주형

최민기
⚡️ 기능 구현
Auth 페이지
react-hook-form, zod를 사용하여 form의 상태관리
유효한 정규식이 아닐 경우 에러 텍스트 출력
모든 필수 input이 유효할 경우 submit 버튼 활성화
react-query useMutation를 사용하여 서버 상태관리
회원 가입 페이지 Loading UI 구현
로그인 페이지 유효한 요청이 아닐 경우 Error Modal
로그인 페이지 유효한 요청시 Home으로 이동
Main 페이지
react-query를 사용하여 상품 랭킹 페이지 구현
useInfiniteQuery와 react-intersection-observer를 사용하여 감지
싱품 상세 페이지
react-slick를 사용하여 상품 상세페이지 이미지 슬라이드 구현
next-ui modal을 사용하여 상품 옵션 선택 모달 구현
recoil을 사용하여 상품 옵션 수량 선택 구현
Typescript를 사용하여 컴포넌트 타입 정의
공통된 모달의 props을 AppModalType로 정의하여 상속
AppPromptModalProps 인터페이스를 actionButtonProps subButtonProps 정의하여 각 버튼의 action과 style, text를 받아 모달안에 있는 닫기 이벤트 적용
장바구니 페이지
recoil을 사용하여 장바구니 리스트 정보를 스토어에 저장하여 전체 선택 및 선택 구매 구현
구매하기 버튼 클릭 시 구매하기 페이지로 이동
구매하기 페이지
react-daum-postcode을 사용하여 배송지 입력하기 페이지의 주소 입력 모달 구현
Toss Payment를 사용하여 결제 기능 구현
recoil을 사용하여 배송 메세지 구현
📦 폴더구조

XENO_backend
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ com
   │  │     └─ daewon
   │  │        └─ xeno_backend
   │  │           ├─ GlobalExceptionHandler.java
   │  │           ├─ XenoBackendApplication.java
   │  │           ├─ config
   │  │           │  ├─ AwsConfig.java
   │  │           │  ├─ CustomSecurityConfig.java
   │  │           │  ├─ RootConfig.java
   │  │           │  └─ SwaggerConfig.java
   │  │           ├─ controller
   │  │           │  ├─ AuthController.java
   │  │           │  ├─ BrandController.java
   │  │           │  ├─ CartController.java
   │  │           │  ├─ HealthController.java
   │  │           │  ├─ LikeController.java
   │  │           │  ├─ ManagerController.java
   │  │           │  ├─ OrdersController.java
   │  │           │  ├─ ProductController.java
   │  │           │  ├─ ProductPublicController.java
   │  │           │  ├─ ReplyController.java
   │  │           │  ├─ ReviewController.java
   │  │           │  ├─ ReviewImageController.java
   │  │           │  ├─ ReviewPublicController.java
   │  │           │  ├─ TestController.java
   │  │           │  └─ UserController.java
   │  │           ├─ domain
   │  │           │  ├─ BaseEntity.java
   │  │           │  ├─ Cart.java
   │  │           │  ├─ DeliveryTrack.java
   │  │           │  ├─ LikeProducts.java
   │  │           │  ├─ Orders.java
   │  │           │  ├─ OrdersRefund.java
   │  │           │  ├─ PaymentMethod.java
   │  │           │  ├─ Products.java
   │  │           │  ├─ ProductsBrand.java
   │  │           │  ├─ ProductsImage.java
   │  │           │  ├─ ProductsLike.java
   │  │           │  ├─ ProductsOption.java
   │  │           │  ├─ ProductsStar.java
   │  │           │  ├─ RefreshToken.java
   │  │           │  ├─ Reply.java
   │  │           │  ├─ Review.java
   │  │           │  ├─ ReviewImage.java
   │  │           │  └─ auth
   │  │           │     ├─ Brand.java
   │  │           │     ├─ BrandApproval.java
   │  │           │     ├─ Customer.java
   │  │           │     ├─ Level.java
   │  │           │     ├─ Manager.java
   │  │           │     ├─ UserRole.java
   │  │           │     └─ Users.java
   │  │           ├─ dto
   │  │           │  ├─ CartRequestDTO.java
   │  │           │  ├─ UploadImageReadDTO.java
   │  │           │  ├─ auth
   │  │           │  │  ├─ AuthSigninDTO.java
   │  │           │  │  ├─ AuthSignupDTO.java
   │  │           │  │  ├─ BrandApprovalDTO.java
   │  │           │  │  ├─ BrandDTO.java
   │  │           │  │  ├─ BrandInfoCardDTO.java
   │  │           │  │  ├─ BrandSignupDTO.java
   │  │           │  │  ├─ GetOneDTO.java
   │  │           │  │  ├─ TokenDTO.java
   │  │           │  │  └─ UserSignupDTO.java
   │  │           │  ├─ cart
   │  │           │  │  ├─ AddToCartDTO.java
   │  │           │  │  ├─ CartDTO.java
   │  │           │  │  ├─ CartSummaryDTO.java
   │  │           │  │  └─ CartUpdateDTO.java
   │  │           │  ├─ manager
   │  │           │  │  ├─ BrandApproveListDTO.java
   │  │           │  │  ├─ BrandListDTO.java
   │  │           │  │  ├─ LevelUpdateDTO.java
   │  │           │  │  ├─ PointUpdateDTO.java
   │  │           │  │  ├─ ProductListDTO.java
   │  │           │  │  ├─ UserInfoDTO.java
   │  │           │  │  └─ UserListDTO.java
   │  │           │  ├─ order
   │  │           │  │  ├─ DeliveryOrdersDTO.java
   │  │           │  │  ├─ OrderCancelDTO.java
   │  │           │  │  ├─ OrderDashBoardInfoDTO.java
   │  │           │  │  ├─ OrderDeliveryInfoReadDTO.java
   │  │           │  │  ├─ OrderInfoByBrandDTO.java
   │  │           │  │  ├─ OrderProductDTO.java
   │  │           │  │  ├─ OrderProductIdsReadDTO.java
   │  │           │  │  ├─ OrdersCardListDTO.java
   │  │           │  │  ├─ OrdersConfirmDTO.java
   │  │           │  │  ├─ OrdersCountDTO.java
   │  │           │  │  ├─ OrdersDTO.java
   │  │           │  │  ├─ OrdersListDTO.java
   │  │           │  │  ├─ OrdersResponseDTO.java
   │  │           │  │  ├─ OrdersSalesAmountDTO.java
   │  │           │  │  ├─ OrdersSalesQuantityDTO.java
   │  │           │  │  ├─ OrdersStatusDTO.java
   │  │           │  │  ├─ OrdersStatusUpdateDTO.java
   │  │           │  │  └─ OrdersTopSellingProductsDTO.java
   │  │           │  ├─ page
   │  │           │  │  ├─ PageInfinityResponseDTO.java
   │  │           │  │  ├─ PageRequestDTO.java
   │  │           │  │  └─ PageResponseDTO.java
   │  │           │  ├─ product
   │  │           │  │  ├─ ProductColorInfoCardDTO.java
   │  │           │  │  ├─ ProductDetailImagesDTO.java
   │  │           │  │  ├─ ProductHeaderDTO.java
   │  │           │  │  ├─ ProductImageDTO.java
   │  │           │  │  ├─ ProductImageInfoDTO.java
   │  │           │  │  ├─ ProductInfoDTO.java
   │  │           │  │  ├─ ProductListByBrandDTO.java
   │  │           │  │  ├─ ProductOrderBarDTO.java
   │  │           │  │  ├─ ProductRegisterDTO.java
   │  │           │  │  ├─ ProductSizeDTO.java
   │  │           │  │  ├─ ProductStockDTO.java
   │  │           │  │  ├─ ProductsInfoCardDTO.java
   │  │           │  │  ├─ ProductsSearchDTO.java
   │  │           │  │  └─ ProductsStarRankListDTO.java
   │  │           │  ├─ reply
   │  │           │  │  ├─ ReplyDTO.java
   │  │           │  │  ├─ ReplyReadDTO.java
   │  │           │  │  └─ ReplyUpdateDTO.java
   │  │           │  ├─ review
   │  │           │  │  ├─ ReviewCardDTO.java
   │  │           │  │  ├─ ReviewCreateDTO.java
   │  │           │  │  ├─ ReviewDTO.java
   │  │           │  │  ├─ ReviewImageDTO.java
   │  │           │  │  ├─ ReviewInfoDTO.java
   │  │           │  │  └─ ReviewUpdateDTO.java
   │  │           │  └─ user
   │  │           │     ├─ BrandUpdateDTO.java
   │  │           │     └─ UserUpdateDTO.java
   │  │           ├─ exception
   │  │           │  ├─ BrandNotFoundException.java
   │  │           │  ├─ InvalidPointException.java
   │  │           │  ├─ ProductNotFoundException.java
   │  │           │  ├─ UnauthorizedException.java
   │  │           │  └─ UserNotFoundException.java
   │  │           ├─ repository
   │  │           │  ├─ CartRepository.java
   │  │           │  ├─ DeliveryTrackRepository.java
   │  │           │  ├─ LikeRepository.java
   │  │           │  ├─ OrdersRefundRepository.java
   │  │           │  ├─ OrdersRepository.java
   │  │           │  ├─ Products
   │  │           │  │  ├─ ProductsBrandRepository.java
   │  │           │  │  ├─ ProductsImageRepository.java
   │  │           │  │  ├─ ProductsLikeRepository.java
   │  │           │  │  ├─ ProductsOptionRepository.java
   │  │           │  │  ├─ ProductsRepository.java
   │  │           │  │  ├─ ProductsSearchRepository.java
   │  │           │  │  └─ ProductsStarRepository.java
   │  │           │  ├─ RefreshTokenRepository.java
   │  │           │  ├─ ReplyRepository.java
   │  │           │  ├─ ReviewImageRepository.java
   │  │           │  ├─ ReviewRepository.java
   │  │           │  └─ auth
   │  │           │     ├─ BrandApprovalRepository.java
   │  │           │     ├─ BrandRepository.java
   │  │           │     ├─ CustomerRepository.java
   │  │           │     ├─ ManagerRepository.java
   │  │           │     └─ UserRepository.java
   │  │           ├─ security
   │  │           │  ├─ UsersDetailsService.java
   │  │           │  ├─ exception
   │  │           │  │  ├─ AccessTokenException.java
   │  │           │  │  ├─ ProductNotFoundException.java
   │  │           │  │  ├─ RefreshTokenException.java
   │  │           │  │  └─ ReviewNotFoundException.java
   │  │           │  ├─ filter
   │  │           │  │  ├─ LoginFilter.java
   │  │           │  │  ├─ RefreshTokenFilter.java
   │  │           │  │  └─ TokenCheckFilter.java
   │  │           │  └─ handler
   │  │           │     ├─ Custom403Handler.java
   │  │           │     └─ UserLoginSuccessHandler.java
   │  │           ├─ service
   │  │           │  ├─ AuthService.java
   │  │           │  ├─ AuthServiceImpl.java
   │  │           │  ├─ CartService.java
   │  │           │  ├─ CartServiceImpl.java
   │  │           │  ├─ DeliveryStatusSchedulerService.java
   │  │           │  ├─ ExcelService.java
   │  │           │  ├─ LikeService.java
   │  │           │  ├─ LikeServiceImpl.java
   │  │           │  ├─ ManagerService.java
   │  │           │  ├─ ManagerServiceImpl.java
   │  │           │  ├─ OrdersService.java
   │  │           │  ├─ OrdersServiceImpl.java
   │  │           │  ├─ ProductService.java
   │  │           │  ├─ ProductServiceImpl.java
   │  │           │  ├─ ReplyService.java
   │  │           │  ├─ ReplyServiceImpl.java
   │  │           │  ├─ ReviewService.java
   │  │           │  ├─ ReviewServiceImpl.java
   │  │           │  └─ S3Service.java
   │  │           └─ utils
   │  │              ├─ CartAndOrderSecurityUtils.java
   │  │              ├─ CategoryUtils.java
   │  │              ├─ JWTUtil.java
   │  │              ├─ ProductSecurityUtils.java
   │  │              └─ ReviewAndReplySecurityUtils.java
   │  └─ resources
   │     ├─ application-prod.properties
   │     └─ application.properties  

