package com.cmwebgame.handle;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.ServletContext;

import com.cmwebgame.ControllerUtils;

public interface MemberSessionHandle {

//	public void RefreshAndSetAttribute(ServletContext context);
	
	public void refreshSession();
	
	public Object checkAutoLogin(Object memberSession) throws IOException, ClassNotFoundException;
	
	public void configureMemberSession(Object memberSession, Object member) ;
	
	public void checkSSO(Object memberSession) ;
	
	public Object restoreMemberSession();
	
	public boolean isLogged();
	
	public boolean isModerator();
	
	public void destroyedMemberSession(String sessionId);
	
	public void setMemberSessionToContext(ServletContext context);
	
	public BufferedImage getCaptchaImage();
	
	public void writeCaptchaImage(Object memberSession);
}
