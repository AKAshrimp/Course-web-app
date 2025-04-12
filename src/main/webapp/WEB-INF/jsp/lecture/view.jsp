<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<jsp:include page="../layout/base.jsp">
    <jsp:param name="title" value="${lecture.title}" />
    <jsp:param name="content" value="lecture/viewContent.jsp" />
</jsp:include> 