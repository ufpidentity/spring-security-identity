<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
  <head>
    <title>Login</title>
  </head>

  <body onload="document.f.j_username.focus();">
    <h1>Login</h1>

    <%-- this form-login-page form is also used as the
         form-error-page to ask for a login again.
         --%>
    <c:if test="${(not empty param.login_message) && (not empty SPRING_SECURITY_LAST_EXCEPTION.message)}">
      <font color="red">
        Your login attempt was not successful, try again.<br/><br/>
        Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
      </font>
    </c:if>

    <c:if test="${not empty IDENTITY_DISPLAY_MESSAGE}">
      <font color="green">
        Please continue.<br/><br/>
        Reason: <c:out value="${IDENTITY_DISPLAY_MESSAGE}"/>.
      </font>
    </c:if>

    <form name="f" action="<c:url value='j_spring_security_check'/>" method="POST">
      <table>
        <c:choose>
        <c:when test="${(not empty IDENTITY_DISPLAY_ITEMS) && (not empty SPRING_SECURITY_LAST_USERNAME)}">
          <tr><td align="right">User:</td><td align="left"><b><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></b></td></tr>
          <tr><td colspan="2" align="right"><input type="hidden" name="j_username" value='<c:out value="${SPRING_SECURITY_LAST_USERNAME}"/>'/></td></tr>
        </c:when>
        <c:otherwise>
          <tr><td align="right">User:</td><td><input type="text" name="j_username" value=""/></td></tr>
        </c:otherwise>
        </c:choose>
        <c:if test="${not empty IDENTITY_DISPLAY_ITEMS}">
        <c:forEach var="displayItem" items="${IDENTITY_DISPLAY_ITEMS}">
          <tr>
            <td align="right"><abbr title="<c:out value='${displayItem.nickname}'/>"><c:out value='${displayItem.displayName}'/></abbr>:</td><td><c:out value='${displayItem.formElement}' escapeXml="false"/></td>
          </tr>
        </c:forEach>
        </c:if>
        <tr><td colspan='2'><input name="submit" type="submit"></td></tr>
        <tr><td colspan='2'><input name="reset" type="reset"></td></tr>
      </table>
    </form>
  </body>
</html>
