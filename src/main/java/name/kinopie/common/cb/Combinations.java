package name.kinopie.common.cb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 要素の組み合わせの集合を表すクラス
 * 
 * @param <E> 要素の型。当該要素は一般契約に従ってequalsとhashCodeを実装した型である必要がある。
 */
public class Combinations<E> {

	private List<Set<E>> combinations = Collections.synchronizedList(new ArrayList<Set<E>>());

	/**
	 * このCombinationsに指定された要素の組み合わせを追加します。
	 * 
	 * @param elements 要素
	 */
	@SafeVarargs
	public final void add(E... elements) {
		Set<E> combination = Sets.newHashSet(elements);
		combinations.add(combination);
	}

	/**
	 * このCombinationsに指定された要素の組み合わせが存在するかどうかを順不同で判定します。
	 * 
	 * @param elements 要素
	 */
	@SafeVarargs
	public final boolean exists(E... elements) {
		Set<E> combination = Sets.newHashSet(elements);
		return combinations.contains(combination);
	}

	/**
	 * このCombinationsに第二引数で指定された要素の組み合わせが存在するかどうかを順不同で判定し、存在しない場合のみ第一引数のRunnableを実行します。
	 * 
	 * @param elements 要素
	 */
	@SafeVarargs
	public final <T> void runIfCombinationNotExists(Runnable runnable, E... elements) {
		if (exists(elements)) {
			return;
		}
		runnable.run();
		add(elements);
	}
}
