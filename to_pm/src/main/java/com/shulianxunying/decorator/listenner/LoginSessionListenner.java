package com.shulianxunying.decorator.listenner;

import com.shulianxunying.decorator.LoginInterceptor;
import com.shulianxunying.entity.PMUser;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * Created by SuChang on 2017/4/26 18:01.
 */
public class LoginSessionListenner implements HttpSessionAttributeListener {
    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        String name = event.getName();

        if (name.equals("user")) {
            PMUser user = (PMUser) event.getValue();
            if (LoginInterceptor.getMap().get(user.get_id()) != null) {
                HttpSession session = LoginInterceptor.getMap().get(user.get_id());
                if (!event.getSession().getId().equals(session.getId())) {
                    session.removeAttribute(user.get_id());
                    session.invalidate();
                }
            }
            LoginInterceptor.getMap().put(user.get_id(), event.getSession());
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {

    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {

    }
}
