package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlClass;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class WebRequests {

    private static final String CLASS_NAME = "webapi_response";

    private final static String CLASS_CODE_RESPONSE = "class " + CLASS_NAME + "(status, entity, cookies) {\n" +
                                                      "    this.status := status;\n" +
                                                      "    this.entity := entity;\n" +
                                                      "    this.cookies := cookies;\n" +
                                                      "}";

    public static SetlObject post(State state, String targetUrl, HashMap<String, String> formData, SetlSet cookieData) throws SetlException {
        Value classCandidate = state.findValue(CLASS_NAME);
        if (classCandidate == Om.OM) {
            Block block = ParseSetlX.parseStringToBlock(state, CLASS_CODE_RESPONSE);
            block.execute(state);
            classCandidate = state.findValue(CLASS_NAME);
        }

        if (classCandidate.isClass() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Value bound to variable '" + CLASS_NAME + "' is not a class: " + classCandidate.toString(state));
        }

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(targetUrl);
        Invocation.Builder request = target.request();

        request = setCookieData(state, cookieData, request);

        Response response = request.post(Entity.form(new MultivaluedHashMap<>(formData)));
        return mapResponse(state, (SetlClass) classCandidate, response);
    }

    private static Invocation.Builder setCookieData(State state, SetlSet cookieData, Invocation.Builder request) throws SetlException {
        if (cookieData.isMap() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("CookieData is not a map: " + cookieData.toString(state));
        }
        for (Value e : cookieData) {
            SetlList entry = (SetlList) e;
            request = request.cookie(entry.getMember(1).getUnquotedString(state), entry.getMember(2).getUnquotedString(state));
        }
        return request;
    }

    private static SetlObject mapResponse(State state, SetlClass responseClass, Response response) throws SetlException {
        List<Value> argumentValues = new ArrayList<>();
        argumentValues.add(Rational.valueOf(response.getStatus()));
        argumentValues.add(new SetlString(response.readEntity(String.class)));
        argumentValues.add(mapCookies(state, response.getCookies().values()));
        return (SetlObject) responseClass.call(state, argumentValues, null, null, null);
    }

    private static SetlSet mapCookies(State state, Collection<NewCookie> responseCookies) {
        SetlSet cookies = new SetlSet();

        for (NewCookie responseCookie : responseCookies) {
            SetlList cookiePair = new SetlList();
            cookiePair.addMember(state, new SetlString(responseCookie.getName()));
            cookiePair.addMember(state, new SetlString(responseCookie.getValue()));

            cookies.addMember(state, cookiePair);
        }

        return cookies;
    }
}
