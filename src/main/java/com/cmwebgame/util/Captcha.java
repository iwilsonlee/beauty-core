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
 * This file creation date: Nov 13, 2004 / 17:17:09
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */

package com.cmwebgame.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.cmwebgame.GPortalExecutionContext;
import com.cmwebgame.handle.MemberSessionHandle;
import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.ImplGlobals;
import com.cmwebgame.util.preferences.SystemGlobals;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.GradientBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomRangeColorGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.SimpleTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator; 
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.GimpyFactory;

/**
 * @author James Yong
 * @version $Id: Captcha.java,v 1.13 2007/08/06 15:38:01 rafaelsteil Exp $
 */
public class Captcha extends ListImageCaptchaEngine {
	private static final Logger logger = Logger.getLogger(Captcha.class);
	
	private MemberSessionHandle memberSessionHandle = (MemberSessionHandle) ImplGlobals.getHandle("MemberSessionHandleImpl");

	private static Captcha classInstance = new Captcha();
	private List backgroundGeneratorList;
	private List textPasterList;
	private List fontGeneratorList;

	private static String charsInUse;

	private void initializeChars() {
		if (SystemGlobals.getBoolValue(ConfigKeys.CAPTCHA_IGNORE_CASE)) {
			charsInUse = "abcdefghijlmnopkrstuvxzyk";
		} else {
			charsInUse = "ABCDEFGHJLKMNPRSTWXYZabcdefghijlmnopkrstuvxzyk";
		}
	}

	/**
	 * Gets the singleton
	 * 
	 * @return Instance of Captcha class
	 */
	public static Captcha getInstance() {
		return classInstance;
	}

	protected void buildInitialFactories() {
		this.initializeChars();

		this.backgroundGeneratorList = new ArrayList();
		this.textPasterList = new ArrayList();
		this.fontGeneratorList = new ArrayList();

		int width = SystemGlobals.getIntValue(ConfigKeys.CAPTCHA_WIDTH);
		int height = SystemGlobals.getIntValue(ConfigKeys.CAPTCHA_HEIGHT);
		int minWords = SystemGlobals.getIntValue(ConfigKeys.CAPTCHA_MIN_WORDS);
		int maxWords = SystemGlobals.getIntValue(ConfigKeys.CAPTCHA_MAX_WORDS);
		int minFontSize = SystemGlobals.getIntValue(ConfigKeys.CAPTCHA_MIN_FONT_SIZE);
		int maxFontSize = SystemGlobals.getIntValue(ConfigKeys.CAPTCHA_MAX_FONT_SIZE);

		//		this.backgroundGeneratorList.add(new GradientBackgroundGenerator(new Integer(width), 
		//				new Integer(height), Color.PINK, Color.LIGHT_GRAY));
		//		this.backgroundGeneratorList.add(new GradientBackgroundGenerator(new Integer(width), 
		//				new Integer(height), Color.WHITE, Color.RED));
		//		this.backgroundGeneratorList.add(new GradientBackgroundGenerator(new Integer(width), 
		//				new Integer(height), Color.ORANGE, Color.LIGHT_GRAY));
		//		this.backgroundGeneratorList.add(new GradientBackgroundGenerator(new Integer(width), 
		//				new Integer(height), Color.CYAN, Color.LIGHT_GRAY));
		//只用白色作為底色
		this.backgroundGeneratorList.add(new GradientBackgroundGenerator(new Integer(width), new Integer(height),
				Color.WHITE, Color.WHITE));

		//this.backgroundGeneratorList.add(new FunkyBackgroundGenerator(new Integer(250), new Integer(50)));

		//		this.textPasterList.add(new RandomTextPaster(new Integer(minWords), new Integer(maxWords), Color.DARK_GRAY));
		//		this.textPasterList.add(new RandomTextPaster(new Integer(minWords), new Integer(maxWords), Color.BLUE));
		//		this.textPasterList.add(new RandomTextPaster(new Integer(minWords), new Integer(maxWords), Color.GREEN));
		//		this.textPasterList.add(new RandomTextPaster(new Integer(minWords), new Integer(maxWords), Color.MAGENTA));
		//		this.textPasterList.add(new RandomTextPaster(new Integer(minWords), new Integer(maxWords), Color.WHITE));
		//只用黑色字體
		//		this.textPasterList.add(new RandomTextPaster(new Integer(minWords), new Integer(maxWords), Color.BLACK));
		RandomRangeColorGenerator cgen = new RandomRangeColorGenerator(new int[] { 0, 100 }, new int[] { 0, 100 },new int[] { 0, 100 });
		this.textPasterList.add(new SimpleTextPaster(new Integer(minWords), new Integer(maxWords), cgen,false));

//		Font[] fontsList = new Font[]{ new Font("Arial", 0, 10), new Font("Tahoma", 0, 10),
//				new Font("Verdana", 0, 10), };
		//只用一種字體
		Font[] fontsList = new Font[]{ new Font("Lucida Sans Unicode", Font.BOLD, 9)};
		//		this.fontGeneratorList.add(new TwistedAndShearedRandomFontGenerator(new Integer(minFontSize), new Integer(maxFontSize)));
		this.fontGeneratorList.add(new RandomFontGenerator(new Integer(minFontSize),new Integer(maxFontSize), fontsList));

		// Create a random word generator
		WordGenerator words = new RandomWordGenerator(charsInUse);

		for (Iterator fontIter = this.fontGeneratorList.iterator(); fontIter.hasNext();) {
			FontGenerator fontGeny = (FontGenerator) fontIter.next();

			for (Iterator backIter = this.backgroundGeneratorList.iterator(); backIter.hasNext();) {
				BackgroundGenerator bkgdGeny = (BackgroundGenerator) backIter.next();

				for (Iterator textIter = this.textPasterList.iterator(); textIter.hasNext();) {
					TextPaster textPaster = (TextPaster) textIter.next();

					WordToImage word2image = new ComposedWordToImage(fontGeny, bkgdGeny, textPaster);

					// Creates a ImageCaptcha Factory
					ImageCaptchaFactory factory = new GimpyFactory(words, word2image);

					// Add a factory to the gimpy list (A Gimpy is a ImagCaptcha)
					addFactory(factory);
				}
			}
		}
	}

	public void writeCaptchaImage() {
		BufferedImage image = memberSessionHandle.getCaptchaImage();

		if (image == null) {
			return;
		}

		OutputStream outputStream = null;

		try {
			outputStream = GPortalExecutionContext.getResponse().getOutputStream();
			ImageIO.write(image, "jpg", outputStream);
		} catch (IOException ex) {
			logger.error(ex);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException ex) {
				}
			}
		}
	}
	/**
	 * @author wilson
	 */
	public void writeCaptchaImage(Object memberSession) {
		memberSessionHandle.writeCaptchaImage(memberSession);
	}
}
