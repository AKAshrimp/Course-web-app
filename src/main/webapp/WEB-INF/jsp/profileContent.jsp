<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="card mb-4">
    <div class="card-header">
        <h4 class="mb-0"><spring:message code="profile.title" /></h4>
    </div>
    <div class="card-body">
        <c:if test="${param.success != null}">
            <div class="alert alert-success" role="alert">
                <spring:message code="profile.updateSuccess" />
            </div>
        </c:if>
        
        <form:form action="/profile/update" method="post" modelAttribute="user">
            <form:hidden path="id" />
            <form:hidden path="username" />
            <form:hidden path="password" />
            <form:hidden path="roles" />
            
            <div class="mb-3">
                <label for="username" class="form-label"><spring:message code="profile.username" /></label>
                <input type="text" class="form-control" id="username" value="${user.username}" disabled>
                <div class="form-text"><spring:message code="profile.usernameNote" /></div>
            </div>
            
            <div class="mb-3">
                <label for="fullName" class="form-label"><spring:message code="profile.fullName" /></label>
                <form:input path="fullName" id="fullName" class="form-control" required="true" />
                <form:errors path="fullName" cssClass="text-danger" />
            </div>
            
            <div class="mb-3">
                <label for="email" class="form-label"><spring:message code="profile.email" /></label>
                <form:input path="email" id="email" type="email" class="form-control" required="true" />
                <form:errors path="email" cssClass="text-danger" />
            </div>
            
            <div class="mb-3">
                <label for="phoneNumber" class="form-label"><spring:message code="profile.phoneNumber" /></label>
                <form:input path="phoneNumber" id="phoneNumber" class="form-control" />
                <form:errors path="phoneNumber" cssClass="text-danger" />
            </div>
            
            <div class="mb-3">
                <button type="submit" class="btn btn-primary"><spring:message code="profile.save" /></button>
            </div>
        </form:form>
    </div>
</div>

<!-- 修改密码卡片 -->
<div class="card">
    <div class="card-header">
        <h4 class="mb-0"><spring:message code="profile.changePassword" /></h4>
    </div>
    <div class="card-body">
        <c:if test="${param.pwdsuccess != null}">
            <div class="alert alert-success" role="alert">
                <spring:message code="profile.passwordChangeSuccess" />
            </div>
        </c:if>
        <c:if test="${param.pwderror != null}">
            <div class="alert alert-danger" role="alert">
                <spring:message code="profile.passwordChangeError" />
            </div>
        </c:if>
        
        <form action="/profile/change-password" method="post" id="passwordForm">
            <div class="mb-3">
                <label for="newPassword" class="form-label"><spring:message code="profile.newPassword" /></label>
                <input type="password" class="form-control" id="newPassword" name="newPassword" required>
            </div>
            
            <div class="mb-3">
                <label for="confirmPassword" class="form-label"><spring:message code="profile.confirmPassword" /></label>
                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                <div id="passwordMatchError" class="text-danger d-none"><spring:message code="profile.passwordMismatch" /></div>
            </div>
            
            <div class="mb-3">
                <button type="submit" class="btn btn-primary" id="changePasswordBtn"><spring:message code="profile.savePassword" /></button>
            </div>
        </form>
    </div>
</div>

<script>
document.getElementById('passwordForm').addEventListener('submit', function(e) {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const errorElement = document.getElementById('passwordMatchError');
    
    if (newPassword !== confirmPassword) {
        e.preventDefault();
        errorElement.classList.remove('d-none');
    } else {
        errorElement.classList.add('d-none');
    }
});
</script> 