package com.cmwebgame.util;

import static com.google.common.base.Preconditions.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.cmwebgame.entities.BlogPost;
import com.cmwebgame.entities.Person;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * about guava test
 * @author wilson
 *
 */
public class GuavaTest {

	@Rule
	public ExpectedException thrown= ExpectedException.none();
	
	@Test
	public void testSetsAndMaps(){
		HashSet<Integer> setA = Sets.newHashSet(1, 2, 3, 4, 5);
		HashSet<Integer> setB = Sets.newHashSet(4, 5, 6, 7, 8);
		 
		SetView<Integer> union = Sets.union(setB, setA);
		System.out.println("union:");
		for (Integer integer : union)
		    System.out.print(integer + " | ");        
		 
		SetView<Integer> difference = Sets.difference(setB, setA);//得到setB中相对setA的不同元素
		System.out.println("difference:");
		for (Integer integer : difference)
		    System.out.println(integer  + " | ");       
		 
		SetView<Integer> intersection = Sets.intersection(setA, setB);
		System.out.println("intersection:");
		for (Integer integer : intersection)
		    System.out.println(integer  + " | ");
	}
	
	@Test
	public void testMultimap() {
		Map<Person, List<BlogPost>> map = new HashMap<Person, List<BlogPost>>();
		Person author = new Person().createPerson("Lee", "Wilson", "537000");
		BlogPost blogPost = new BlogPost().createBlogPost("this is the title",
				"some content in here", "2014-8-19 12:11:34");
		BlogPost blogPost2 = new BlogPost().createBlogPost("aaaaaa",
				"gffdgdfgdgfd", "2014-8-19 18:17:34");
		
		List<BlogPost> blogPosts = map.get(author);
		if (blogPosts == null) {
			blogPosts = new ArrayList<BlogPost>();
			map.put(author, blogPosts);
		}
		blogPosts.add(blogPost);
		blogPosts.add(blogPost2);
		
		System.out.println("1 the person-blogpost map is : " + map.toString());
		
		Multimap<Person, BlogPost> multimap = ArrayListMultimap.create();//一个 key 对应多个 value 的数据结构
		multimap.put(author, blogPost);
		multimap.put(author, blogPost2);
		System.out.println("2 the person-blogpost map is : " + multimap.toString());
		
	}
	
	@Test
	public void testMultiset(){
		List<String> wordList = Lists.newArrayList("the","the","word","hello");
		Map<String, Integer> map = new HashMap<String, Integer>(); 
		 for(String word : wordList){ 
		    Integer count = map.get(word); 
		    map.put(word, (count == null) ? 1 : count + 1); 
		 } 
		 //count word “the”
		 Integer count = map.get("the");
		 System.out.println("count word “the” in map :" + count);
		 
		 HashMultiset<String> multiset = HashMultiset.create();
		 multiset.addAll(wordList);
		 count = multiset.count("the");
		 System.out.println("count word “the” in multiset :" + count);
		 multiset.setCount("the", 2 , 4);//将符合原有重复个数的元素修改为新的重复次数,若没有符合原有重复指定个数的则维持原有实际个数
		 count = multiset.count("the");
		 System.out.println("count word “the” in multiset2 :" + count);
		 for(String em : multiset){
			 System.out.print(em + " | ");
		 }
		 System.out.println();
		 System.out.print("the multiset size is : " + multiset.size());
	}
	
	@Test
	public void testImmutableCollections(){
		Set<String> sets = new HashSet<String>();
		Set<String> setss = Sets.newHashSet("222","111","333","111");
		sets.add("222");
		sets.add("111");
		sets.add("333");
		
		setss.add("123");
		for(String em : setss){
			System.out.print(em + " | ");
		}
		System.out.println();
		ImmutableSet<String> immutableSet1 = ImmutableSet.copyOf(setss);
		for(String em : immutableSet1){
			System.out.print(em + " | ");
		}
		System.out.println();
		Builder<String>  builder = ImmutableSet.builder(); 
		ImmutableSet<String> immutableSet2 = builder.add("RED").addAll(setss).build();
		for(String em : immutableSet2){
			System.out.print(em + " | ");
		}
		System.out.println();
		ImmutableSet<String> immutableSet = ImmutableSet.of("RED", "GREEN");
		assertFalse(immutableSet.containsAll(ImmutableSet.of("REddD","GREEN")));
		
		thrown.expect(UnsupportedOperationException.class);//定义预期出现的异常为UnsupportedOperationException
		//thrown.expectMessage("ImmutableSet can not add any element！");//定义预期出现的异常信息内容
		immutableSet.add("qqqq");//此处程序预期会抛出UnsupportedOperationException异常
		
	}
	
	@Test
	public void testOptional() {
		Optional<String> nameOptional = Optional.fromNullable(null);
		assertNull("is null", nameOptional.orNull());
//		assertTrue("name is present true", nameOptional.isPresent());
		assertEquals("name is empty","wilson", nameOptional.or("wilson"));
//		assertEquals("name is empty","", nameOptional.get());
		
	}
	
	/**
	 * test the com.google.common.base.Preconditions.check* method
	 */
	@Test
	public void testPreconditions(){
		List<String> list = Lists.newArrayList("sdsd","aa","bb","cc","dd");
		checkNotNull("", "is null");
		/*
		 * 使用 checkArgument 與 checkState 的差別除了一個會丟出 IllegalArgumentException，一個是丟出 IllegalStateException 之外，
		 * 最主要的是在語義差別，checkArgument 名稱表明這個方法是用於檢查引數，而 checkState 名稱表明，這個方法是用於檢查物件的狀態。
		 */
		checkArgument(!list.isEmpty(),"is empty");
		checkState(99 <= 100, "超過負載");
		checkElementIndex(4, list.size());
		checkPositionIndex(5,list.size());//index大於size時才會丟出例外
		checkPositionIndexes(2, 5, list.size());
	}
	
	@Test
	public void testObjects(){
		Person person = new Person().createPerson("lee", "wilson", "537000");
		Person person1 = new Person().createPerson("lee", "wilson", "537000");
		System.out.println(person);
		System.out.println(person.equals(person1));
		System.out.println("person hashcode : " + person.hashCode());
		System.out.println("person1 hashcode : " + person1.hashCode());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRange(){
		System.out.println(Range.closed(1, 20));
		System.out.println(Range.closed('a', 'h'));
		System.out.println(Range.atLeast('h'));
		for(int i : ContiguousSet.create(Range.closed(1, 20), DiscreteDomain.integers())) {
			System.out.print(i);
		}
		System.out.println();
		for(Object i : ContiguousSet.create(Range.closed('a', 'h'), LowerCaseDomain.letters())) {
			System.out.print(i);
		}
		System.out.println();
		for(Object i : ContiguousSet.create(Range.atLeast('h'), LowerCaseDomain.letters())) {
			System.out.print(i);
		}
		System.out.println();
	}

}
