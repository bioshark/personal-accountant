package org.roly.personalaccountant.domain.notifiers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ReactiveListTest {

    @Test
    void shouldPropagateListenerException() {
        ReactiveList<String> list = new ReactiveList<>();
        list.registerListener(element -> {
            throw new IllegalStateException("listener failed");
        });

        assertThatThrownBy(() -> list.add("x"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("listener failed");
    }
}
