package com.cmwebgame.entities;

import com.google.common.base.Objects;

public class Person{
	private String fistName;
	private String lastName;
	private String zipCode;
	
	public Person person() {
		return this;
	}
	
	public Person createPerson(String fistName,String lastName,String zipCode){
		this.fistName = fistName;
		this.lastName = lastName;
		this.zipCode = zipCode;
		return this;
	}
	
	@Override
	public String toString(){
		return Objects.toStringHelper(this)
				.add("fistName", fistName)
				.add("lastName", lastName)
				.add("zipCode", zipCode)
				.toString();
				
	}
	
	@Override
    public boolean equals(Object that) {
        if(that instanceof Person) {
        	Person p = (Person) that;
            return Objects.equal(fistName, p.fistName) && Objects.equal(lastName, p.lastName);
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
        return Objects.hashCode(fistName, lastName);
    }
}