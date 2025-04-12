<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h4 class="mb-0"><spring:message code="nav.login"/></h4>
            </div>
            <div class="card-body">
                <c:if test="${param.error != null}">
                    <div class="alert alert-danger" role="alert">
                        <spring:message code="login.error"/>
                    </div>
                </c:if>
                <c:if test="${param.logout != null}">
                    <div class="alert alert-success" role="alert">
                        <spring:message code="login.logout.success"/>
                    </div>
                </c:if>
                <c:if test="${registerSuccess}">
                    <div class="alert alert-success" role="alert">
                        <spring:message code="${message}"/>
                    </div>
                </c:if>
                
                <form action="/login" method="post">
                    <div class="mb-3">
                        <label for="username" class="form-label"><spring:message code="user.username"/></label>
                        <input type="text" class="form-control" id="username" name="username" required>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label"><spring:message code="user.password"/></label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    <div class="mb-3">
                        <button type="submit" class="btn btn-primary"><spring:message code="nav.login"/></button>
                    </div>
                </form>
                
                <div class="mt-3">
                    <p><spring:message code="login.noAccount"/> <a href="/register"><spring:message code="nav.register"/></a></p>
                </div>
            </div>
        </div>
    </div>
</div> 