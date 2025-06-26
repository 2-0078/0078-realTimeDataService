package com.pieceofcake.real_time_data.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    /**
     * 200: 요청 성공
     **/
    SUCCESS(HttpStatus.OK, true, 200, "요청에 성공하였습니다."),

    /**
     * 400 : security 에러
     */
    WRONG_JWT_TOKEN(HttpStatus.UNAUTHORIZED, false, 401, "다시 로그인 해주세요"),
    NO_SIGN_IN(HttpStatus.UNAUTHORIZED, false, 402, "로그인을 먼저 진행해주세요"),
    NO_ACCESS_AUTHORITY(HttpStatus.FORBIDDEN, false, 403, "접근 권한이 없습니다"),
    DISABLED_USER(HttpStatus.FORBIDDEN, false, 404, "비활성화된 계정입니다. 계정을 복구하시겠습니까?"),
    FAILED_TO_RESTORE(HttpStatus.INTERNAL_SERVER_ERROR, false, 405, "계정 복구에 실패했습니다. 관리자에게 문의해주세요."),
    NO_EXIST_OAUTH(HttpStatus.NOT_FOUND, false, 406, "소셜 로그인 정보가 존재하지 않습니다."),

    /**
     * 900: 기타 에러
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, 900, "Internal server error"),
    SSE_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, false, 901, "알림 전송에 실패하였습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, false, 902, "유효하지 입력입니다"),
    FAILED_TO_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, false, 903, "저장에 실패했습니다."),

    /**
     * 2000: users service error
     */
    // token
    TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, false, 2001, "토큰이 유효하지 않습니다."),

    // Users
    DUPLICATED_USER(HttpStatus.CONFLICT, false, 2101, "이미 가입된 멤버입니다."),
    FAILED_TO_LOGIN(HttpStatus.UNAUTHORIZED, false, 2102, "아이디 또는 패스워드를 다시 확인하세요."),
    DUPLICATED_SOCIAL_USER(HttpStatus.CONFLICT, false, 2103, "이미 소셜 연동된 계정입니다."),
    DUPLICATED_SOCIAL_PROVIDER_USER(HttpStatus.CONFLICT, false, 2104, "계정에 동일한 플랫폼이 이미 연동되어있습니다"),
    NO_EXIST_USER(HttpStatus.NOT_FOUND, false, 2105, "존재하지 않는 멤버 정보입니다."),
    PASSWORD_SAME_FAILED(HttpStatus.BAD_REQUEST, false, 2106, "현재 사용중인 비밀번호 입니다."),
    PASSWORD_CONTAIN_NUM_FAILED(HttpStatus.BAD_REQUEST, false, 2107, "휴대폰 번호를 포함한 비밀번호 입니다."),
    PASSWORD_MATCH_FAILED(HttpStatus.BAD_REQUEST, false, 2108, "패스워드를 다시 확인해주세요."),
    NO_SUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, false, 2109, "지원하지 않는 플랫폼입니다"),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, false, 2010, "이미 사용중인 닉네임입니다."),
    SAME_NICKNAME(HttpStatus.CONFLICT, false, 2011, "현재 사용중인 닉네임입니다."),
    INVALID_EMAIL_ADDRESS(HttpStatus.BAD_REQUEST, false, 2012, "이메일을 다시 확인해주세요."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, false, 2013, "잘못된 인증코드입니다."),
    FAILED_TO_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, false, 2014, "이메일 전송에 실패했습니다."),
    DUPLICATED_PASSWORD(HttpStatus.CONFLICT, false, 2015, "기존 패스워드와 동일합니다."),
    FAILED_TO_SIGN_UP(HttpStatus.BAD_REQUEST, false, 2016, "회원가입에 실패했습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, false, 2017, "잘못된 형식의 비밀번호입니다."),
    INVALID_LOGIN_ID_FORMAT(HttpStatus.BAD_REQUEST, false, 2018, "잘못된 형식의 아이디입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, false, 2019, "잘못된 형식의 이메일입니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, false, 2020, "잘못된 형식의 닉네임입니다."),
    INVALID_PHONE_FORMAT(HttpStatus.BAD_REQUEST, false, 2021, "잘못된 형식의 전화번호입니다."),
    INVALID_GENDER_FORMAT(HttpStatus.BAD_REQUEST, false, 2022, "성별은 '남성' 또는 '여성'만 가능합니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, false, 2023, "잘못된 형식의 날짜입니다."),
    INVALID_BIRTH_PAST(HttpStatus.BAD_REQUEST, false, 2024, "생년월일은 과거 날짜여야 합니다."),
    INVALID_NAME_FORMAT(HttpStatus.BAD_REQUEST, false, 2025, "잘못된 형식의 이름입니다."),
    DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, false, 2026, "이미 사용중인 아이디입니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, false, 2027, "이미 사용중인 이메일입니다."),
    DUPLICATED_PHONE(HttpStatus.CONFLICT, false, 2028, "이미 사용중인 전화번호입니다."),
    DUPLICATED_LOGIN(HttpStatus.UNAUTHORIZED, false, 2030, "이미 다른 기기에서 로그인되었습니다."),

    /**
     * 3000: product service error
     */

    // Product
    NO_EXIST_PRODUCT(HttpStatus.NOT_FOUND, false, 3001, "존재하지 않는 상품입니다"),
    NO_EXIST_OPTION(HttpStatus.NOT_FOUND, false, 3002, "존재하지 않는 옵션입니다"),
    NO_EXIST_CATEGORY(HttpStatus.NOT_FOUND, false, 3003, "존재하지 않는 카테고리입니다"),
    NO_EXIST_PRODUCT_CATEGORY(HttpStatus.NOT_FOUND, false, 3004, "존재하지 않는 상품 카테고리입니다"),
    NO_EXIST_PIECE_PRODUCT(HttpStatus.NOT_FOUND, false, 3005, "존재하지 않는 조각상품입니다"),

    DUPLICATED_PRODUCT(HttpStatus.CONFLICT, false, 3006, "이미 등록된 상품입니다"),
    DUPLICATED_OPTION(HttpStatus.CONFLICT, false, 3007, "이미 등록된 옵션입니다"),
    DUPLICATED_CATEGORY(HttpStatus.CONFLICT, false, 3008, "이미 등록된 카테고리입니다"),
    DUPLICATED_RESTOCK_NOTIFICATION(HttpStatus.CONFLICT, false, 3009, "이미 재입고 알림 신청이 완료 되었습니다"),
    INVALID_RESTOCK_NOTIFICATION_CONDITION(HttpStatus.BAD_REQUEST, false, 3010, "재입고 알림은 품절 상품에만 신청할 수 있습니다."),

    NO_EXIST_OPTIONS_IN_PRODUCT(HttpStatus.NOT_FOUND, false, 3011, "해당 상품에 옵션이 존재하지 않습니다"),
    NOT_ENOUGH_STOCK(HttpStatus.CONFLICT, false, 3012, "해당 상품에 재고가 충분하지 않습니다"),

    // Cart
    CART_PRODUCT_KIND_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, false, 3100, "장바구니에 담을 수 있는 상품 종류는 최대 20개까지입니다"),
    CART_PRODUCT_QUANTITY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, false, 3101, "해당 상품은 장바구니에 담을 수 있는 수량을 초과했습니다."),
    INVALID_CART_ACCESS(HttpStatus.BAD_REQUEST, false, 3102, "유효하지 않거나 접근 권한이 없는 장바구니 항목입니다."),
    DUPLICATE_CART_OPTION(HttpStatus.CONFLICT, false, 3103, "이미 동일한 옵션의 상품이 장바구니에 존재합니다."),

    /**
     * 4000: comment service error
     */

    // Comment
    NO_EXIST_COMMENT(HttpStatus.NOT_FOUND, false, 4001, "존재하지 않는 댓글입니다"),
    NO_DELETE_COMMENT_AUTHORITY(HttpStatus.BAD_REQUEST, false, 4002, "댓글 삭제 권한이 없습니다"),
    NO_DELETE_RE_COMMENT_AUTHORITY(HttpStatus.BAD_REQUEST, false, 4003, "대댓글 삭제 권한이 없습니다"),
    NO_EXIST_RE_COMMENT(HttpStatus.NOT_FOUND, false, 4003, "존재하지 않는 대댓글입니다"),
    NO_EXIST_PIN_AUTHORITY(HttpStatus.BAD_REQUEST, false, 4004, "고정 권한이 없습니다"),

    /**
     * 5000: notification service error
     */

    // Notification
    NO_EXIST_NOTIFICATION_SETTING(HttpStatus.NOT_FOUND, false, 5001, "유저의 알림 설정이 존재하지 않습니다."),
    EXIST_NOTIFICATION_SETTING(HttpStatus.BAD_REQUEST, false, 5002, "유저의 알림 설정이 이미 존재합니다."),
    NO_EXIST_NOTIFICATION(HttpStatus.NOT_FOUND, false, 5003, "존재하지 않는 알림입니다."),
    CANNOT_SHARE(HttpStatus.BAD_REQUEST, false, 5004, "공유할 수 없는 유저입니다."),
    SUCCESS_TO_SSE_CONNECT(HttpStatus.OK, true, 5005, "sse 연결에 성공했습니다."),

    /**
     * 6000: gpt-api error
     */
    // Media
    NO_EXIST_MEDIA(HttpStatus.NOT_FOUND, false, 6001, "존재하지 않는 미디어입니다"),

    /**
     * 6000: gpt-api error
     */
    // S3
    S3_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, false, 7001, "파일 업로드에 실패하였습니다."),

    /**
     * 결제관련 ERROR
     * 10000 ~
     */
    //StarbucksCard
    DUPLICATED_STARBUCKS_CARD(HttpStatus.CONFLICT, false, 10001, "이미 등록된 카드 정보입니다."),
    NO_EXIST_STARBUCKS_CARD(HttpStatus.NOT_FOUND, false, 10002, "카드 정보가 존재하지 않습니다."),
    INVALID_STARBUCKS_CARD(HttpStatus.BAD_REQUEST, false, 10003, "카드 정보가 일치하지 않습니다."),
    NO_CREATION_STARBUCKS_CARD(HttpStatus.BAD_REQUEST, false, 10004, "카드 생성 중 오류가 발생했습니다."),
    NO_DELETE_STARBUCKS_CARD(HttpStatus.BAD_REQUEST, false, 10005, "카드 삭제 중 오류가 발생했습니다."),
    NO_CHARGE_STARBUCKS_CARD(HttpStatus.NOT_FOUND, false, 10006, "카드 잔액이 부족합니다."),

    // 결제
    PAYMENT_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, false, 10010, "결제 인증에 실패했습니다."),
    INVALID_ORDER_ID(HttpStatus.BAD_REQUEST, false, 10011, "유효하지 않은 주문번호입니다."),
    AMOUNT_MISMATCH(HttpStatus.CONFLICT, false, 10012, "결제 금액이 일치하지 않습니다."),
    TEMPORARY_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, false, 10013, "임시 결제 정보가 존재하지 않습니다."),
    PAYMENT_APPROVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 10014, "결제 승인에 실패했습니다."),
    PAYMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 10015, "결제 정보 저장에 실패했습니다."),
    PAYMENT_CANCELLED(HttpStatus.BAD_REQUEST, false, 10016, "사용자에 의해 결제가 취소되었습니다."),

    // 배송지
    DUPLICATED_ADDRESS(HttpStatus.CONFLICT, false, 10020, "기존 배송지와 동일합니다."),
    FAILED_TO_SAVE_ADDRESS(HttpStatus.INTERNAL_SERVER_ERROR, false, 10021, "배송지 저장에 실패헀습니다."),
    NO_EXIST_ADDRESS(HttpStatus.NOT_FOUND, false, 10022, "존재하지 않는 배송지입니다."),
    INVALID_ZIP_NO_FORMAT(HttpStatus.BAD_REQUEST, false, 10023, "잘못된 형식의 우편번호입니다."),
    INVALID_ADDRESS_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, false, 10024, "잘못된 형식의 배송지 별칭입니다."),
    INVALID_DELIVERY_MEMO_FORMAT(HttpStatus.BAD_REQUEST, false, 10025, "잘못된 형식의 배송 메모입니다."),
    INVALID_TOTAL_ADDRESS_FORMAT(HttpStatus.BAD_REQUEST, false, 10026, "잘못된 형식의 전체 주소입니다."),
    INVALID_MEMBER_UUID(HttpStatus.BAD_REQUEST, false, 10027, "잘못된 형식의 사용자 UUID 입니다."),
    INVALID_ADDRESS_UUID(HttpStatus.BAD_REQUEST, false, 10028, "잘못된 형식의 배송지 UUID 입니다."),
    ADDRESS_QUANTITY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, false, 10029, "저장 가능한 배송지 개수를 초과했습니다."),
    FAILED_TO_DELETE_MAIN_ADDRESS(HttpStatus.BAD_REQUEST, false, 10030, "기본 배송지는 삭제가 불가능합니다."),
    INVALID_ROAD_ADDRESS_FORMAT(HttpStatus.BAD_REQUEST, false, 10031, "잘못된 형식의 도로명 주소입니다."),
    INVALID_DETAIL_ADDRESS_FORMAT(HttpStatus.BAD_REQUEST, false, 10032, "잘못된 형식의 상세 주소입니다."),

    // 약관
    NO_EXIST_AGREEMENT(HttpStatus.NOT_FOUND, false, 10040, "존재하지 않는 약관입니다."),
    FAILED_TO_DELETE_AGREEMENT(HttpStatus.BAD_REQUEST, false, 10041, "삭제할 수 없는 약관입니다."),

    //구매
    PURCHASE_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 20010, "구매 정보 생성에 실패했습니다."),
    PURCHASE_NOT_FOUND(HttpStatus.NOT_FOUND, false, 20011, "해당 구매 정보를 찾을 수 없습니다."),
    PURCHASE_PRODUCT_LIST_EMPTY(HttpStatus.BAD_REQUEST, false, 20012, "구매 상품 목록이 비어 있습니다."),
    COUPON_INVALID(HttpStatus.BAD_REQUEST, false, 20015, "유효하지 않은 쿠폰입니다."),
    GIFT_CERTIFICATION_INVALID(HttpStatus.BAD_REQUEST, false, 20016, "유효하지 않은 기프트 인증 번호입니다."),
    PURCHASE_DUPLICATE(HttpStatus.NOT_FOUND, false, 20019, "이미 처리된 구매 요청입니다."),

    //시즌
    NO_EXIST_SEASON(HttpStatus.NOT_FOUND, false, 20030, "시즌 정보가 존재하지 않습니다."),
    NO_EXIST_PRODUCT_SEASON(HttpStatus.NOT_FOUND, false, 20031, "상품 시즌 정보가 존재하지 않습니다."),

    // 기획전(이벤트)
    NO_EXIST_EVENT(HttpStatus.NOT_FOUND, false, 20040, "기획전 정보가 존재하지 않습니다."),
    NO_EXIST_PRODUCT_EVENT(HttpStatus.NOT_FOUND, false, 20041, "상품 기획전 정보가 존재하지 않습니다.");


    private final HttpStatusCode httpStatusCode;
    private final boolean isSuccess;
    private final int code;
    private final String message;

}
