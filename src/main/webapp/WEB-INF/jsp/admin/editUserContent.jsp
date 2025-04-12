<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container mt-5">
    <div class="row">
        <div class="col-lg-8 mx-auto">
            <div class="card shadow-sm">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h4 class="mb-0"><i class="bi bi-person-gear me-2"></i><spring:message code="user.edit.title" /></h4>
                    <a href="/admin/dashboard" class="btn btn-sm btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> <spring:message code="button.back" />
                    </a>
                </div>
                
                <div class="card-body p-4">
                    <form action="/admin/users/edit/${user.id}" method="post" class="needs-validation" novalidate>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="username" class="form-label">
                                    <i class="bi bi-person me-1"></i> <spring:message code="user.username" />
                                </label>
                                <input type="text" class="form-control" id="username" name="username" value="${user.username}" readonly>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="password" class="form-label">
                                    <i class="bi bi-key me-1"></i> <spring:message code="user.password" />
                                </label>
                                <input type="password" class="form-control" id="password" name="password" value="************">
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="fullName" class="form-label">
                                <i class="bi bi-person-badge me-1"></i> <spring:message code="user.fullName" />
                            </label>
                            <input type="text" class="form-control" id="fullName" name="fullName" value="${user.fullName}" required>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="email" class="form-label">
                                    <i class="bi bi-envelope me-1"></i> <spring:message code="user.email" />
                                </label>
                                <input type="email" class="form-control" id="email" name="email" value="${user.email}" required>
                            </div>
                            
                            <div class="col-md-6 mb-3">
                                <label for="phoneNumber" class="form-label">
                                    <i class="bi bi-telephone me-1"></i> <spring:message code="user.phoneNumber" />
                                </label>
                                <input type="text" class="form-control" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}">
                            </div>
                        </div>
                        
                        <div class="mb-4">
                            <label class="form-label">
                                <i class="bi bi-shield-lock me-1"></i> <spring:message code="user.role" />
                            </label>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="form-check ps-0">
                                        <div class="d-flex align-items-center p-2 border rounded mb-2 ${user.roles.contains('ROLE_USER') ? 'border-success bg-success bg-opacity-10' : ''}">
                                            <input class="form-check-input me-3" type="checkbox" name="roles" value="ROLE_USER" id="roleUser" 
                                                <c:if test="${user.roles.contains('ROLE_USER')}">checked</c:if>>
                                            <label class="form-check-label w-100" for="roleUser">
                                                <spring:message code="role.ROLE_USER" />
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-check ps-0">
                                        <div class="d-flex align-items-center p-2 border rounded ${user.roles.contains('ROLE_TEACHER') ? 'border-success bg-success bg-opacity-10' : ''}">
                                            <input class="form-check-input me-3" type="checkbox" name="roles" value="ROLE_TEACHER" id="roleTeacher" 
                                                <c:if test="${user.roles.contains('ROLE_TEACHER')}">checked</c:if>>
                                            <label class="form-check-label w-100" for="roleTeacher">
                                                <spring:message code="role.ROLE_TEACHER" />
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                            <button type="submit" class="btn btn-primary px-4">
                                <i class="bi bi-save me-2"></i> <spring:message code="button.save" />
                            </button>
                            <a href="/admin/dashboard" class="btn btn-secondary">
                                <i class="bi bi-x-circle me-2"></i> <spring:message code="button.cancel" />
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div> 