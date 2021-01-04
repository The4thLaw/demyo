<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
    <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <link rel="icon" href="${pageContext.request.contextPath}/favicon.ico">
    <title>Demyo</title>
    <link href="${pageContext.request.contextPath}/css/${appCssFilename}" rel="preload" as="style">
    <link href="${pageContext.request.contextPath}/css/${vendorCssFilename}" rel="preload" as="style">
    <link href="${pageContext.request.contextPath}/js/${appJsFilename}" rel="modulepreload" as="script">
    <link href="${pageContext.request.contextPath}/js/${vendorJsFilename}" rel="modulepreload" as="script">
    <link href="${pageContext.request.contextPath}/css/${appCssFilename}" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/${vendorCssFilename}" rel="stylesheet">
</head>
<body data-contextRoot="${pageContext.request.contextPath}/" data-apiRoot="${pageContext.request.contextPath}/api/">
    <noscript>
        <strong>We're sorry but the Vue frontend to Demyo doesn't work properly without JavaScript enabled. Please enable it to continue.</strong>
    </noscript>
    <div id=app></div>
    <script type="module" src="${pageContext.request.contextPath}/js/${vendorJsFilename}"></script>
    <script type="module" src="${pageContext.request.contextPath}/js/${appJsFilename}"></script>
    <script>
    !function(){var e=document,t=e.createElement("script");if(!("noModule"in t)&&"onbeforeload"in t){var n=!1;e.addEventListener("beforeload",function(e){if(e.target===t)n=!0;else if(!e.target.hasAttribute("nomodule")||!n)return;e.preventDefault()},!0),t.type="module",t.src=".",e.head.appendChild(t),t.remove()}}();
    </script>
    <script src="${pageContext.request.contextPath}/js/${vendorLegacyJsFilename}" nomodule></script>
    <script src="${pageContext.request.contextPath}/js/${appLegacyJsFilename}" nomodule></script>
</body>
</html>
