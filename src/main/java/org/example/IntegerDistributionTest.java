package org.example;

import org.example.IntegerDistribution.Bound;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItems;

public class IntegerDistributionTest {

    @ParameterizedTest
    @MethodSource("provideInvalidDistribution")
    public void test_validateDistributionConfigString_should_fail(String distribution,
                                                                  Class<IllegalArgumentException> expected) {
        IntegerDistribution mysteryBox = new IntegerDistribution(distribution);
        Assertions.assertThrows(
                expected,
                mysteryBox::parseDistributionConfig
        );
    }

    private static Stream<Arguments> provideInvalidDistribution() {
        return Stream.of(
                Arguments.of("0.5=1000,0.3=5000,0.15=10000,0.05=1000000a", IllegalArgumentException.class),
                Arguments.of("0.5=1000,0.3=5000,0.15=10000,0.05axy=1000000", IllegalArgumentException.class),
                Arguments.of("", IllegalArgumentException.class),
                Arguments.of("  ", IllegalArgumentException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidDistribution")
    void isBlank_ShouldReturnTrueForNullOrBlankStrings(String distribution, IntegerDistribution target) {
        /// act
        IntegerDistribution actual = new IntegerDistribution(distribution);
        actual.parseDistributionConfig();

        /// assert
        Assertions.assertEquals(target.getBoundNumber(), actual.getBoundNumber());
        Assertions.assertEquals(target.getDistribution(), actual.getDistribution());
        MatcherAssert.assertThat(target.getBounds(), hasItems(
                actual.getBounds().get(0),
                actual.getBounds().get(1),
                actual.getBounds().get(2),
                actual.getBounds().get(3)
        ));
    }

    private static Stream<Arguments> provideValidDistribution() {
        List<Bound> boundList1 = new LinkedList<>();
        boundList1.add(new Bound(5, 1, 5, 1_000_000));
        boundList1.add(new Bound(15, 6, 20, 10_000));
        boundList1.add(new Bound(30, 21, 50, 5_000));
        boundList1.add(new Bound(50, 51, 100, 1_000));

        Arguments arg1 = Arguments.of(
                "0.5=1000,0.3=5000,0.15=10000,0.05=1000000",
                new IntegerDistribution(100, "0.5=1000,0.3=5000,0.15=10000,0.05=1000000", boundList1));

        List<Bound> boundList2 = new LinkedList<>();
        boundList2.add(new Bound(5, 1, 5, 1_000_000));
        boundList2.add(new Bound(195, 6, 200, 10_000));
        boundList2.add(new Bound(300, 201, 500, 5_000));
        boundList2.add(new Bound(500, 501, 1000, 1_000));

        Arguments arg2 = Arguments.of(
                "0.5=1000,0.3=5000,0.195=10000,0.005=1000000",
                new IntegerDistribution(1000, "0.5=1000,0.3=5000,0.195=10000,0.005=1000000", boundList2));

        return Stream.of(arg1, arg2);
    }
}
