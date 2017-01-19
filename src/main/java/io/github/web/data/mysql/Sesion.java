package io.github.web.data.mysql;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION)
public class Sesion 
{
	private String info;
	public void setInfo(String info) {
	this.info = info;
	}
	public String getInfo() {
	return info;
	}
}
