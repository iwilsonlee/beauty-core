package com.cmwebgame.entities;

import com.google.common.base.Objects;

public class BlogPost{
	private String title;
	private String content;
	private String createTime;
	
	public BlogPost person() {
		return this;
	}
	
	public BlogPost createBlogPost(String title,String content,String createTime){
		this.title = title;
		this.content = content;
		this.createTime = createTime;
		return this;
	}
	
	@Override
	public String toString(){
		return Objects.toStringHelper(this)
				.add("title", title)
				.add("content", content)
				.add("createTime", createTime)
				.toString();
				
	}
	
	@Override
    public boolean equals(Object that) {
        if(that instanceof BlogPost) {
        	BlogPost p = (BlogPost) that;
            return Objects.equal(title, p.title) && Objects.equal(content, p.content);
        }
        return false;
    }
	
	/*
	 * 在同一個應用程式執行期間，對同一物件呼叫 hashCode 方法，必須回傳相同的整數結果。
     * 如果兩個物件使用 equals(Object) 測試結果為相等, 則這兩個物件呼叫 hashCode 時，必須獲得相同的整數結果。
     * 如果兩個物件使用 equals(Object) 測試結果為不相等, 則這兩個物件呼叫 hashCode 時，可以獲得不同的整數結果。
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
    public int hashCode() {
        return Objects.hashCode(title, content);
    }
}