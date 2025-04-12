<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:include page="../layout/base.jsp">
    <jsp:param name="title" value="Teacher Dashboard" />
    <jsp:param name="content" value="admin/dashboardContent.jsp" />
</jsp:include> 