$(function(){
    check_account();
});

function check_account() {
	var errorInfo = [
		"用户名错误",
		"密码错误"
	   ];
	var errorFlag = $("#errorFlag").val();
	$("#errorInfo").html(errorInfo[parseInt(errorFlag)-1]);
}

