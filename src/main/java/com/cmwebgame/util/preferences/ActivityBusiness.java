/*
 * Copyright (c) CMWEBGAME Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on May 29, 2004 by pieter
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame.util.preferences;

/**
* 
* @author wilson
* @version 
*/

public class ActivityBusiness 
{
	public static final int STATUS_COUNT = 4;
	public static final String[] STATUS = {"未設置", "未領取", "已領取", "刪除"};
	public static final int NOT_SET = 0;
	public static final int NOT_GET = 1;
	public static final int ALREADY_GET = 2;
	public static final int DELETE = 3;
	
	

	public static final int TYPE_COUNT = 15;
	public static final String[] TYPES = {"未設置","天策農場","天策博雅","帝國農場","帝國博雅","天策Winwin","帝國Winwin","天策地圖日記",
		"天策Yahoo","帝國地圖日記","天策聘禮包","天策恐龍王國","天策游戲基地","英雄遠征農場","英雄遠征下小小戰爭"};

	public static final int TYPE_NONE = 0;
	public static final int TYPE_TC_ROJO = 1;
	public static final int TYPE_TC_BOYA = 2;
	public static final int TYPE_DG_ROJO = 3;
	public static final int TYPE_DG_BOYA = 4;
	public static final int TYPE_TC_WINWIN = 5;
	public static final int TYPE_DG_WINWIN = 6;
	public static final int TYPE_TC_ATLASPOST = 7;
	public static final int TYPE_TC_YAHOO = 8;
	public static final int TYPE_DG_ATLASPOST = 9;
	public static final int TYPE_TC_PINLI = 10;
	public static final int TYPE_TC_KINGDOM = 11;
	public static final int TYPE_TC_GAMEBASE = 12;
	public static final int TYPE_YX_ROJO = 13;
	public static final int TYPE_YX_XXZZ = 14;

	

	private ActivityBusiness() {}
}
