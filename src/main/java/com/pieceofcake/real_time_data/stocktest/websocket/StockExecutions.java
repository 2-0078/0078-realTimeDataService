package com.pieceofcake.real_time_data.stocktest.websocket;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockExecutions {
    private String mksc_shrn_iscd;     // 유가증권 단축 종목코드
    private String stck_cntg_hour;     // 주식 체결 시간
    private Float stck_prpr;          // 주식 현재가
    private String prdy_vrss_sign;     // 전일 대비 부호
    private Float prdy_vrss;          // 전일 대비
    private Float prdy_ctrt;          // 전일 대비율
    private Float wghn_avrg_stck_prc; // 가중 평균 주식 가격
    private Float stck_oprc;          // 주식 시가
    private Float stck_hgpr;          // 주식 최고가
    private Float stck_lwpr;          // 주식 최저가
    private Float askp1;              // 매도호가1
    private Float bidp1;              // 매수호가1
    private Float cntg_vol;           // 체결 거래량
    private Float acml_vol;           // 누적 거래량
    private Float acml_tr_pbmn;       // 누적 거래 대금
    private Float seln_cntg_csnu;     // 매도 체결 건수
    private Float shnu_cntg_csnu;     // 매수 체결 건수
    private Float ntby_cntg_csnu;     // 순매수 체결 건수
    private Float cttr;               // 체결강도
    private Float seln_cntg_smtn;     // 총 매도 수량
    private Float shnu_cntg_smtn;     // 총 매수 수량
    private String ccld_dvsn;          // 체결구분
    private Float shnu_rate;          // 매수비율
    private Float prdy_vol_vrss_acml_vol_rate; // 전일 거래량 대비 등락율
    private String oprc_hour;          // 시가 시간
    private String oprc_vrss_prpr_sign; // 시가대비구분
    private Float oprc_vrss_prpr;     // 시가대비
    private String hgpr_hour;          // 최고가 시간
    private String hgpr_vrss_prpr_sign; // 고가대비구분
    private Float hgpr_vrss_prpr;     // 고가대비
    private String lwpr_hour;          // 최저가 시간
    private String lwpr_vrss_prpr_sign; // 저가대비구분
    private Float lwpr_vrss_prpr;     // 저가대비
    private String bsop_date;          // 영업 일자
    private String new_mkop_cls_code;  // 신 장운영 구분 코드
    private String trht_yn;            // 거래정지 여부
    private Float askp_rsqn1;         // 매도호가 잔량1
    private Float bidp_rsqn1;         // 매수호가 잔량1
    private Float total_askp_rsqn;    // 총 매도호가 잔량
    private Float total_bidp_rsqn;    // 총 매수호가 잔량
    private Float vol_tnrt;           // 거래량 회전율
    private Float prdy_smns_hour_acml_vol;      // 전일 동시간 누적 거래량
    private Float prdy_smns_hour_acml_vol_rate; // 전일 동시간 누적 거래량 비율
    private String hour_cls_code;      // 시간 구분 코드
    private String mrkt_trtm_cls_code; // 임의종료구분코드
    private Float vi_stnd_prc;        // 정적VI발동기준가

    public static StockExecutions toDto(String message) {
        String[] p = message.split("\\^");
        if (p.length < 46) return null; // 필드 개수에 따라 조정 필요

        String stockCode = p[0].split("\\|")[3];

        return StockExecutions.builder()
                .mksc_shrn_iscd(stockCode)
                .stck_cntg_hour(p[1])
                .stck_prpr(parseFloat(p[2]))
                .prdy_vrss_sign(p[3])
                .prdy_vrss(parseFloat(p[4]))
                .prdy_ctrt(parseFloat(p[5]))
                .wghn_avrg_stck_prc(parseFloat(p[6]))
                .stck_oprc(parseFloat(p[7]))
                .stck_hgpr(parseFloat(p[8]))
                .stck_lwpr(parseFloat(p[9]))
                .askp1(parseFloat(p[10]))
                .bidp1(parseFloat(p[11]))
                .cntg_vol(parseFloat(p[12]))
                .acml_vol(parseFloat(p[13]))
                .acml_tr_pbmn(parseFloat(p[14]))
                .seln_cntg_csnu(parseFloat(p[15]))
                .shnu_cntg_csnu(parseFloat(p[16]))
                .ntby_cntg_csnu(parseFloat(p[17]))
                .cttr(parseFloat(p[18]))
                .seln_cntg_smtn(parseFloat(p[19]))
                .shnu_cntg_smtn(parseFloat(p[20]))
                .ccld_dvsn(p[21])
                .shnu_rate(parseFloat(p[22]))
                .prdy_vol_vrss_acml_vol_rate(parseFloat(p[23]))
                .oprc_hour(p[24])
                .oprc_vrss_prpr_sign(p[25])
                .oprc_vrss_prpr(parseFloat(p[26]))
                .hgpr_hour(p[27])
                .hgpr_vrss_prpr_sign(p[28])
                .hgpr_vrss_prpr(parseFloat(p[29]))
                .lwpr_hour(p[30])
                .lwpr_vrss_prpr_sign(p[31])
                .lwpr_vrss_prpr(parseFloat(p[32]))
                .bsop_date(p[33])
                .new_mkop_cls_code(p[34])
                .trht_yn(p[35])
                .askp_rsqn1(parseFloat(p[36]))
                .bidp_rsqn1(parseFloat(p[37]))
                .total_askp_rsqn(parseFloat(p[38]))
                .total_bidp_rsqn(parseFloat(p[39]))
                .vol_tnrt(parseFloat(p[40]))
                .prdy_smns_hour_acml_vol(parseFloat(p[41]))
                .prdy_smns_hour_acml_vol_rate(parseFloat(p[42]))
                .hour_cls_code(p[43])
                .mrkt_trtm_cls_code(p[44])
                .vi_stnd_prc(parseFloat(p[45]))
                .build();
    }


    // 안전한 Float 파싱 헬퍼 메서드
    private static Float parseFloat(String s) {
        try {
            return s != null && !s.isBlank() ? Float.parseFloat(s) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
