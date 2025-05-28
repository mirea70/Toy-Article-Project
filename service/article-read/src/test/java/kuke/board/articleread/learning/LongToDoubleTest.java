package kuke.board.articleread.learning;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class LongToDoubleTest {
    @Test
    void longToDoubleTest() {
        // long은 64비트로 정수
        // double은 64비트로 부동소수점
        // -> long은 정수이기 때문에 큰 숫자도 정밀하게 표현 가능
        // -> double은 소수를 표현해야하기에 동일한 숫자도 차지하는 데이터 크기가 큼 -> 따라서 너무 큰 숫자의 경우 데이터 유실 가능성 존재
        long longValue = 111_111_111_111_111_111L;
        System.out.println("longValue = " + longValue);
        double doubleValue = longValue;
        System.out.println("doubleValue = " + new BigDecimal(doubleValue).toString());
        long longValue2 = (long) doubleValue;
        System.out.println("longValue2 = " + longValue2);
    }
}
