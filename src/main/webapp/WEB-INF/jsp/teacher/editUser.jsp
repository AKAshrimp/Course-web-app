<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../layout/header.jsp">
    <jsp:param name="title" value="编辑用户" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card">
                <div class="card-header bg-warning text-dark">
                    <h3><i class="bi bi-person-gear"></i> 编辑用户</h3>
                </div>
                <div class="card-body">
                    <form:form action="/teacher/users/edit/${user.id}" method="post" modelAttribute="user" class="needs-validation">
                        <div class="mb-3">
                            <label for="username" class="form-label">用户名</label>
                            <form:input path="username" id="username" class="form-control" readonly="true" />
                            <small class="text-muted">用户名不可修改</small>
                        </div>
                        
                        <div class="mb-3">
                            <label for="fullName" class="form-label">姓名 <span class="text-danger">*</span></label>
                            <form:input path="fullName" id="fullName" class="form-control" required="true" />
                            <form:errors path="fullName" cssClass="text-danger" />
                        </div>
                        
                        <div class="mb-3">
                            <label for="email" class="form-label">邮箱 <span class="text-danger">*</span></label>
                            <form:input path="email" id="email" type="email" class="form-control" required="true" />
                            <form:errors path="email" cssClass="text-danger" />
                        </div>
                        
                        <div class="mb-3">
                            <label for="phoneNumber" class="form-label">电话</label>
                            <form:input path="phoneNumber" id="phoneNumber" class="form-control" />
                            <form:errors path="phoneNumber" cssClass="text-danger" />
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">用户角色 <span class="text-danger">*</span></label>
                            <div class="form-check">
                                <form:checkbox path="roles" value="ROLE_STUDENT" id="roleStudent" cssClass="form-check-input" />
                                <label class="form-check-label" for="roleStudent">学生</label>
                            </div>
                            <div class="form-check">
                                <form:checkbox path="roles" value="ROLE_TEACHER" id="roleTeacher" cssClass="form-check-input" />
                                <label class="form-check-label" for="roleTeacher">教师</label>
                            </div>
                            <form:errors path="roles" cssClass="text-danger" />
                        </div>
                        
                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-warning">
                                <i class="bi bi-save"></i> 保存更改
                            </button>
                            <a href="/teacher/users" class="btn btn-secondary">
                                <i class="bi bi-arrow-left"></i> 返回
                            </a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp" /> 