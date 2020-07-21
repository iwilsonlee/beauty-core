package com.cmwebgame.handle;

import com.cmwebgame.context.RequestContext;
import com.cmwebgame.context.ResponseContext;

public interface GlobalHandle {

	public void setAttribute(RequestContext request);
	
	public boolean checkRequest(RequestContext request, ResponseContext response);
	
	public void runService();
	
}
