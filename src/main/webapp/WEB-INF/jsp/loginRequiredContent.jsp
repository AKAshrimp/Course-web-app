<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center py-5">
                    <div class="display-1 text-danger mb-4">
                        <i class="bi bi-lock"></i>
                    </div>
                    <h1 class="h3 mb-4"><spring:message code="login.required.heading"/></h1>
                    <p class="lead mb-4"><spring:message code="login.required.message"/></p>
                    
                    <div class="d-flex gap-3 justify-content-center mt-4">
                        <a href="/login" class="btn btn-primary btn-lg px-4">
                            <i class="bi bi-box-arrow-in-right me-2"></i>
                            <spring:message code="nav.login"/>
                        </a>
                        <a href="/register" class="btn btn-outline-primary btn-lg px-4">
                            <i class="bi bi-person-plus me-2"></i>
                            <spring:message code="nav.register"/>
                        </a>
                    </div>
                    
                    <div class="mt-4">
                        <a href="/" class="btn btn-link text-muted">
                            <i class="bi bi-arrow-left me-2"></i>
                            <spring:message code="button.back.home"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div> 