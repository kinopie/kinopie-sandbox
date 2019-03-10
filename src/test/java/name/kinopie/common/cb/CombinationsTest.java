package name.kinopie.common.cb;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CombinationsTest {

	@Test
	public void test01() {
		Combinations<String> combinations = new Combinations<>();
		combinations.add("a", "b");
		Assert.assertTrue(combinations.exists("a", "b"));
		Assert.assertTrue(combinations.exists("b", "a"));
		Assert.assertFalse(combinations.exists("a", "b", "c"));
		combinations.add("a", null);
		Assert.assertTrue(combinations.exists(null, "a"));
		Assert.assertFalse(combinations.exists(null, "b"));
	}

	@Test
	public void test02() {
		Combinations<List<String>> combinations = new Combinations<>();
		combinations.add(Arrays.asList("a", "b"), Arrays.asList("c", "d"));
		Assert.assertTrue(combinations.exists(Arrays.asList("c", "d"), Arrays.asList("a", "b")));
		Assert.assertFalse(
				combinations.exists(Arrays.asList("a", "b"), Arrays.asList("c", "d"), Arrays.asList("e", "f")));
		combinations.add(Arrays.asList("a", "b"), null);
		Assert.assertTrue(combinations.exists(null, Arrays.asList("a", "b")));
		Assert.assertFalse(combinations.exists(null, Arrays.asList("c", "d")));
	}

	@Test
	public void test03() {
		Combinations<List<String>> combinations = new Combinations<>();
		combinations.add(Arrays.asList("a", null), Arrays.asList("b", null));
		Assert.assertTrue(combinations.exists(Arrays.asList("b", null), Arrays.asList("a", null)));
	}
}
