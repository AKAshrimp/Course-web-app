<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card shadow-sm mb-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h4 class="mb-0">
                        <i class="bi bi-pencil-square me-2"></i>
                        <span style="background: linear-gradient(90deg, var(--primary-color), var(--primary-dark)); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">
                            <spring:message code="poll.edit.title" />
                        </span>
                    </h4>
                    <a href="/admin/dashboard" class="btn btn-sm btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> <spring:message code="button.back" />
                    </a>
                </div>
                
                <div class="card-body p-4">
                    <form:form method="post" modelAttribute="poll" action="/admin/polls/edit/${poll.id}" class="needs-validation">
                        <div class="mb-4">
                            <label for="question" class="form-label fw-bold"><spring:message code="poll.question" /></label>
                            <form:input path="question" id="question" class="form-control form-control-lg" required="true" />
                            <form:errors path="question" cssClass="text-danger mt-2" />
                        </div>
                        
                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="bi bi-save me-2"></i> <spring:message code="button.save" />
                            </button>
                        </div>
                    </form:form>

                    <h4 class="mt-5 mb-3 position-relative d-inline-block">
                        <span style="background: linear-gradient(90deg, var(--primary-color), var(--primary-dark)); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">
                            <i class="bi bi-list-check me-2"></i><spring:message code="poll.options" />
                        </span>
                        <div class="position-absolute" style="height: 2px; width: 50%; bottom: -5px; left: 0; background: linear-gradient(90deg, var(--primary-color), var(--primary-dark));"></div>
                    </h4>
                    
                    <div id="optionsContainer">
                        <c:forEach items="${poll.options}" var="option" varStatus="status">
                            <form action="/admin/polls/edit/${poll.id}" method="post" class="mb-3 option-form">
                                <input type="hidden" name="optionId" value="${option.id}" />
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0 text-primary fw-bold">
                                        <spring:message code="poll.option"/> ${status.index + 1}
                                    </span>
                                    <input type="text" name="optionText" class="form-control" value="${option.text}" required />
                                    <button type="submit" class="btn btn-outline-primary">
                                        <i class="bi bi-check"></i>
                                    </button>
                                    <button type="button" class="btn btn-outline-danger delete-option" data-option-id="${option.id}">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                                <small class="text-muted ms-2 d-block mt-1">
                                    <i class="bi bi-bar-chart-line me-1"></i> <spring:message code="poll.votes"/>: ${option.votes.size()}
                                </small>
                            </form>
                        </c:forEach>
                    </div>
                    
                    <div class="mt-4 p-3 border-top">
                        <form action="/admin/polls/${poll.id}/options/add" method="post" class="mb-3">
                            <div class="input-group">
                                <span class="input-group-text bg-light border-0 text-success fw-bold">
                                    <i class="bi bi-plus-circle me-1"></i> <spring:message code="poll.new.option"/>
                                </span>
                                <input type="text" name="text" class="form-control" required />
                                <button type="submit" class="btn btn-success">
                                    <spring:message code="button.add"/>
                                </button>
                            </div>
                        </form>
                    </div>
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
    
    .input-group {
        box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        border-radius: 0.25rem;
        overflow: hidden;
    }
</style>

<script>
document.addEventListener('DOMContentLoaded', function() {
    // 删除选项按钮事件
    const deleteButtons = document.querySelectorAll('.delete-option');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('<spring:message code="confirm.delete.option"/>')) {
                const optionId = this.dataset.optionId;
                const form = document.createElement('form');
                form.method = 'post';
                form.action = '/admin/polls/options/delete/' + optionId;
                document.body.appendChild(form);
                form.submit();
            }
        });
    });
});
</script> 