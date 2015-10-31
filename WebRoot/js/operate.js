$(function(){
	$("#select").on('click',function(){
		window.location.href = basePath + "operateSelect.action?" + "operate="+$("#operate").val();
	});
	$("#delete").on('click',function(){
		window.location.href = basePath + "operateDelete.action?" + "operate="+$("#operate").val();
	});
	$("#modify").on('click',function(){
		window.location.href = basePath + "operateModify.action?" + "operate="+$("#operate").val();
	});
	$("#add").on('click',function(){
		window.location.href = basePath + "operateAdd.action?" + "operate="+$("#operate").val();
	});
})