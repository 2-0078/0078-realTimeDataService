package com.pieceofcake.real_time_data.stocktest.websocket;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockQuotes {
    String type;
    String mksc_shrn_iscd; // 유가증권 단축 종목코드 //  유가증권 단축 종목코드
    String bsop_hour; // 영업 시간 //  영업 시간
    String hour_cls_code; // 시간 구분 코드 //  시간 구분 코드
    Float askp1; //  매도호가1
    Float askp2; //  매도호가2
    Float askp3; //  매도호가3
    Float askp4; //  매도호가4
    Float askp5; //  매도호가5
    Float askp6; //  매도호가6
    Float askp7; //  매도호가7
    Float askp8; //  매도호가8
    Float askp9; //  매도호가9
    Float askp10; //  매도호가10
    Float bidp1; //  매수호가1
    Float bidp2; //  매수호가2
    Float bidp3; //  매수호가3
    Float bidp4; //  매수호가4
    Float bidp5; //  매수호가5
    Float bidp6; //  매수호가6
    Float bidp7; //  매수호가7
    Float bidp8; //  매수호가8
    Float bidp9; //  매수호가9
    Float bidp10; //  매수호가10
    Float askp_rsqn1; //  매도호가 잔량1
    Float askp_rsqn2; //  매도호가 잔량2
    Float askp_rsqn3; //  매도호가 잔량3
    Float askp_rsqn4; //  매도호가 잔량4
    Float askp_rsqn5; //  매도호가 잔량5
    Float askp_rsqn6; //  매도호가 잔량6
    Float askp_rsqn7; //  매도호가 잔량7
    Float askp_rsqn8; //  매도호가 잔량8
    Float askp_rsqn9; //  매도호가 잔량9
    Float askp_rsqn10; //  매도호가 잔량10
    Float bidp_rsqn1; //  매수호가 잔량1
    Float bidp_rsqn2; //  매수호가 잔량2
    Float bidp_rsqn3; //  매수호가 잔량3
    Float bidp_rsqn4; //  매수호가 잔량4
    Float bidp_rsqn5; //  매수호가 잔량5
    Float bidp_rsqn6; //  매수호가 잔량6
    Float bidp_rsqn7; //  매수호가 잔량7
    Float bidp_rsqn8; //  매수호가 잔량8
    Float bidp_rsqn9; //  매수호가 잔량9
    Float bidp_rsqn10; //  매수호가 잔량10
    Float total_askp_rsqn; //  총 매도호가 잔량
    Float total_bidp_rsqn; //  총 매수호가 잔량
    Float ovtm_total_askp_rsqn; //  시간외 총 매도호가 잔량
    Float ovtm_total_bidp_rsqn; //  시간외 총 매수호가 잔량
    Float antc_cnpr; //  예상 체결가
    Float antc_cnqn; //  예상 체결량
    Float antc_vol; //  예상 거래량
    Float antc_cntg_vrss; //  예상 체결 대비
    String antc_cntg_vrss_sign; //  예상 체결 대비 부호
    Float antc_cntg_prdy_ctrt; //  예상 체결 전일 대비율
    Float acml_vol; //  누적 거래량
    Float total_askp_rsqn_icdc; //  총 매도호가 잔량 증감
    Float total_bidp_rsqn_icdc; //  총 매수호가 잔량 증감
    Float ovtm_total_askp_icdc; //  시간외 총 매도호가 증감
    Float ovtm_total_bidp_icdc; //  시간외 총 매수호가 증감
    String stck_deal_cls_code; //  주식 매매 구분 코드

    public static StockQuotes toDto(String message) {
        String[] p = message.split("\\^");
        if (p.length < 3) return null;  // 필드 수 부족 시 null 반환

        String stockCode = p[0].split("\\|")[3];


        return StockQuotes.builder()
                .type("quotes")
                .mksc_shrn_iscd(stockCode)
                .bsop_hour(p[1])
                .hour_cls_code(p[2])
                .askp1(parseFloat(p[3]))
                .askp2(parseFloat(p[4]))
                .askp3(parseFloat(p[5]))
                .askp4(parseFloat(p[6]))
                .askp5(parseFloat(p[7]))
                .askp6(parseFloat(p[8]))
                .askp7(parseFloat(p[9]))
                .askp8(parseFloat(p[10]))
                .askp9(parseFloat(p[11]))
                .askp10(parseFloat(p[12]))
                .bidp1(parseFloat(p[13]))
                .bidp2(parseFloat(p[14]))
                .bidp3(parseFloat(p[15]))
                .bidp4(parseFloat(p[16]))
                .bidp5(parseFloat(p[17]))
                .bidp6(parseFloat(p[18]))
                .bidp7(parseFloat(p[19]))
                .bidp8(parseFloat(p[20]))
                .bidp9(parseFloat(p[21]))
                .bidp10(parseFloat(p[22]))
                .askp_rsqn1(parseFloat(p[23]))
                .askp_rsqn2(parseFloat(p[24]))
                .askp_rsqn3(parseFloat(p[25]))
                .askp_rsqn4(parseFloat(p[26]))
                .askp_rsqn5(parseFloat(p[27]))
                .askp_rsqn6(parseFloat(p[28]))
                .askp_rsqn7(parseFloat(p[29]))
                .askp_rsqn8(parseFloat(p[30]))
                .askp_rsqn9(parseFloat(p[31]))
                .askp_rsqn10(parseFloat(p[32]))
                .bidp_rsqn1(parseFloat(p[33]))
                .bidp_rsqn2(parseFloat(p[34]))
                .bidp_rsqn3(parseFloat(p[35]))
                .bidp_rsqn4(parseFloat(p[36]))
                .bidp_rsqn5(parseFloat(p[37]))
                .bidp_rsqn6(parseFloat(p[38]))
                .bidp_rsqn7(parseFloat(p[39]))
                .bidp_rsqn8(parseFloat(p[40]))
                .bidp_rsqn9(parseFloat(p[41]))
                .bidp_rsqn10(parseFloat(p[42]))
                .total_askp_rsqn(parseFloat(p[43]))
                .total_bidp_rsqn(parseFloat(p[44]))
                .ovtm_total_askp_rsqn(parseFloat(p[45]))
                .ovtm_total_bidp_rsqn(parseFloat(p[46]))
                .antc_cnpr(parseFloat(p[47]))
                .antc_cnqn(parseFloat(p[48]))
                .antc_vol(parseFloat(p[49]))
                .antc_cntg_vrss(parseFloat(p[50]))
                .antc_cntg_vrss_sign(p[51])
                .antc_cntg_prdy_ctrt(parseFloat(p[52]))
                .acml_vol(parseFloat(p[53]))
                .total_askp_rsqn_icdc(parseFloat(p[54]))
                .total_bidp_rsqn_icdc(parseFloat(p[55]))
                .ovtm_total_askp_icdc(parseFloat(p[56]))
                .ovtm_total_bidp_icdc(parseFloat(p[57]))
                .stck_deal_cls_code(p[58])
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