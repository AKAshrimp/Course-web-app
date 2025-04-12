<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card">
                <div class="card-header bg-warning text-dark">
                    <h3><i class="bi bi-bar-chart-fill"></i> <spring:message code="poll.add.title"/></h3>
                </div>
                <div class="card-body">
                    <form:form action="/admin/polls/add" method="post" modelAttribute="poll">
                        <div class="mb-3">
                            <label for="question" class="form-label"><spring:message code="poll.question"/></label>
                            <form:input path="question" id="question" class="form-control" required="true" />
                            <form:errors path="question" cssClass="text-danger" />
                        </div>
                        
                        <h5 class="mt-4 mb-3"><spring:message code="poll.options"/></h5>
                        <div id="optionsContainer">
                            <div class="mb-3">
                                <label class="form-label"><spring:message code="poll.option"/> 1</label>
                                <input type="text" name="optionText" class="form-control" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label"><spring:message code="poll.option"/> 2</label>
                                <input type="text" name="optionText" class="form-control" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label"><spring:message code="poll.option"/> 3</label>
                                <input type="text" name="optionText" class="form-control" />
                            </div>
                            <div class="mb-3">
                                <label class="form-label"><spring:message code="poll.option"/> 4</label>
                                <input type="text" name="optionText" class="form-control" />
                            </div>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <button type="button" id="addOptionBtn" class="btn btn-outline-secondary">
                                <i class="bi bi-plus-circle"></i> <spring:message code="poll.add.option"/>
                            </button>
                            <button type="submit" class="btn btn-warning">
                                <i class="bi bi-save"></i> <spring:message code="poll.create"/>
                            </button>
                            <a href="/admin/dashboard" class="btn btn-secondary">
                                <i class="bi bi-arrow-left"></i> <spring:message code="button.back"/>
                            </a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const optionsContainer = document.getElementById('optionsContainer');
    const addOptionBtn = document.getElementById('addOptionBtn');
    let optionCount = 4;
    
    addOptionBtn.addEventListener('click', function() {
        optionCount++;
        const newOption = document.createElement('div');
        newOption.className = 'mb-3';
        newOption.innerHTML = `
            <label class="form-label"><spring:message code="poll.option"/> ${optionCount}</label>
            <div class="input-group">
                <input type="text" name="optionText" class="form-control" />
                <button type="button" class="btn btn-outline-danger remove-option">
                    <i class="bi bi-trash"></i>
                </button>
            </div>
        `;
        optionsContainer.appendChild(newOption);
        
        // 添加删除按钮的事件监听
        const removeBtn = newOption.querySelector('.remove-option');
        removeBtn.addEventListener('click', function() {
            optionsContainer.removeChild(newOption);
        });
    });
});
</script> 