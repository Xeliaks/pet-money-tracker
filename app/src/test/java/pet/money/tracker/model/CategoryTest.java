package pet.money.tracker.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void fromString_exactMatch() {
        assertThat(Category.fromString("FOOD")).isEqualTo(Category.FOOD);
    }

    @Test
    void fromString_caseInsensitive() {
        assertThat(Category.fromString("food")).isEqualTo(Category.FOOD);
    }

    @Test
    void fromString_unknown_returnsOther() {
        assertThat(Category.fromString("xyz")).isEqualTo(Category.OTHER);
    }

    @Test
    void fromString_null_returnsOther() {
        assertThat(Category.fromString(null)).isEqualTo(Category.OTHER);
    }

    @Test
    void fromString_blank_returnsOther() {
        assertThat(Category.fromString("   ")).isEqualTo(Category.OTHER);
    }
}