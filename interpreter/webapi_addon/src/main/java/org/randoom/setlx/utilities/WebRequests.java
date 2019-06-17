package org.randoom.setlx.utilities;

import org.apache.cxf.interceptor.Fault;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.JVMException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Rational;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlObject;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class WebRequests {

    private static final String CLASS_NAME = "webapi_response";

    private final static String CLASS_CODE_RESPONSE = "class " + CLASS_NAME + "(status, entity, cookies) {\n" +
                                                      "    this.status := status;\n" +
                                                      "    this.entity := entity;\n" +
                                                      "    this.cookies := cookies;\n" +
                                                      "}";

    public static SetlObject get(State state, String targetUrl, SetlSet queryParameterMap, SetlSet cookieData) throws SetlException {
        Response response = getResponse(state, targetUrl, queryParameterMap, cookieData);
        SetlObject setlObject = mapResponse(state, response, null);
        response.close();
        return setlObject;
    }

    public static SetlObject getAndStoreFile(State state, String targetUrl, SetlSet queryParameterMap, SetlSet cookieData, String fileToWrite) throws SetlException {
        Response response = getResponse(state, targetUrl, queryParameterMap, cookieData);
        SetlObject setlObject;
        if (response.getStatus() == 200) {
            Path targetPath = Paths.get(fileToWrite).toAbsolutePath();
            try (InputStream inputStream = response.readEntity(InputStream.class)) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new JVMIOException("Could not save response to '" + fileToWrite + "'", e);
            }
            setlObject = mapResponse(state, response, targetPath.toString());
        } else {
            setlObject = mapResponse(state, response, null);
        }
        response.close();
        return setlObject;
    }

    private static Response getResponse(State state, String targetUrl, SetlSet queryParameterMap, SetlSet cookieData) throws SetlException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(targetUrl);

        for (Value e : queryParameterMap) {
            SetlList entry = (SetlList) e;
            target = target.queryParam(entry.getMember(1).getUnquotedString(state), entry.getMember(2).getUnquotedString(state));
        }

        Invocation.Builder request = target.request();

        request = setCookieData(state, cookieData, request);

        try {
            disableLoggers();
            return request.get();
        } catch (ProcessingException e) {
            throw new JVMException("Could not perform GET '" + targetUrl + "': " + e.getMessage(), e);
        }
    }

    public static SetlObject post(State state, String targetUrl, SetlSet formDataMap, SetlSet cookieData) throws SetlException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(targetUrl);
        Invocation.Builder request = target.request();

        request = setCookieData(state, cookieData, request);

        HashMap<String, String> formData = new HashMap<>();
        for (Value e : formDataMap) {
            SetlList entry = (SetlList) e;
            formData.put(entry.getMember(1).getUnquotedString(state), entry.getMember(2).getUnquotedString(state));
        }

        Response response;
        try {
            disableLoggers();
            response = request.post(Entity.form(new MultivaluedHashMap<>(formData)));
        } catch (ProcessingException e) {
            throw new JVMException("Could not perform POST '" + targetUrl + "': " + e.getMessage(), e);
        }

        SetlObject setlObject = mapResponse(state, response, null);
        response.close();
        return setlObject;
    }

    private static void disableLoggers() {
        Logger global = Logger.getLogger("");
        Handler[] handlers = global.getHandlers();
        for (Handler handler : handlers) {
            global.removeHandler(handler);
        }
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

    private static SetlObject mapResponse(State state, Response response, String overrideEntity) throws SetlException {
        Value classCandidate = state.findValue(CLASS_NAME);
        if (classCandidate == Om.OM) {
            Block block = ParseSetlX.parseStringToBlock(state, CLASS_CODE_RESPONSE);
            block.execute(state);
            classCandidate = state.findValue(CLASS_NAME);
        }

        if (classCandidate.isClass() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException("Value bound to variable '" + CLASS_NAME + "' is not a class: " + classCandidate.toString(state));
        }

        List<Value> argumentValues = new ArrayList<>();
        argumentValues.add(Rational.valueOf(response.getStatus()));
        if (overrideEntity != null) {
            argumentValues.add(new SetlString(overrideEntity));
        } else {
            argumentValues.add(new SetlString(response.readEntity(String.class)));
        }
        argumentValues.add(mapCookies(state, response.getCookies().values()));
        return (SetlObject) classCandidate.call(state, argumentValues, null, null, null);
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
