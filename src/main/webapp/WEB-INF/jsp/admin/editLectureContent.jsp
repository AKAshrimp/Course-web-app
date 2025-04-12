<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card shadow-sm">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h4 class="mb-0">
                        <i class="bi bi-pencil-square me-2"></i>
                        <span style="background: linear-gradient(90deg, var(--primary-color), var(--primary-dark)); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">
                            <spring:message code="lecture.edit.title" />
                        </span>
                    </h4>
                    <a href="/admin/dashboard" class="btn btn-sm btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> <spring:message code="button.back" />
                    </a>
                </div>
                
                <div class="card-body p-4">
                    <form:form method="post" modelAttribute="lecture" action="/admin/lectures/edit/${lecture.id}" class="needs-validation">
                        <div class="mb-4">
                            <label for="title" class="form-label fw-bold"><spring:message code="lecture.title" /></label>
                            <form:input path="title" id="title" class="form-control form-control-lg" required="true" />
                            <form:errors path="title" cssClass="text-danger mt-2" />
                        </div>
                        
                        <div class="mb-4">
                            <label for="description" class="form-label fw-bold"><spring:message code="lecture.description" /></label>
                            <form:textarea path="description" id="description" class="form-control" rows="6" required="true" />
                            <form:errors path="description" cssClass="text-danger mt-2" />
                        </div>
                        
                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="bi bi-save me-2"></i> <spring:message code="button.save" />
                            </button>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .card {
        border: none;
        transition: transform 0.3s, box-shadow 0.3s;
    }
    
    .card:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0,0,0,0.1);
    }
    
    .form-control:focus {
        border-color: var(--primary-color);
        box-shadow: 0 0 0 0.25rem rgba(25, 135, 84, 0.25);
    }
</style> 