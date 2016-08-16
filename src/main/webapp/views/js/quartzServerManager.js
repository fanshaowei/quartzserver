var quartzServerManager = {
	selectRowData:[]
};

$(document).ready(function(){		
	//初始化管理列表
	quartzServerManager.initDataGrid();
	//获取选中的数据
	quartzServerManager.getSelectData();
	quartzServerManager.getCheckData();
	
	quartzServerManager.listenTriggerType();
	quartzServerManager.onClickEvent();
	
})

//初始化任务管理列表
quartzServerManager.initDataGrid = function(){
	$("#task-list").datagrid({
		width:"400",
		height:"350",
		fitColumns:true,
		fit:true,	
		rownumbers:true,
		singleSelect:false,
		toolbar:[{
			iconCls: "icon-edit-modify",
			text:"编辑任务",
			handler: function(){
				quartzServerManager.editSelectJob();				
			}
		},'-',{
			iconCls: "icon-add",
			text:"添加任务",
			handler: function(){
				quartzServerManager.addJob();
			}
		},'-',{
			iconCls: "icon-dept",//"icon-configure",
			text:"批量添加任务",
			handler: function(){
				quartzServerManager.batchAddJobs();
			}
		},'-',{
			iconCls: "icon-edit-delete",
			 text:"删除任务",
			handler: function(){
				quartzServerManager.deleteJob();
			}
		},'-',{
			iconCls: "icon-stop",
			text:"暂停任务",
			handler: function(){
				quartzServerManager.pauseJobs();
			}
		},'-',{
			iconCls: "icon-start",
			text:"启动任务",
			handler: function(){
				quartzServerManager.resumeJobs();
			}
		}],
		method:"get",
		url: top.Client.CONST_PATH + "/quartzServerManager/getAllJobDetails",
		loadMsg:'loading...'
	});
	 		
}

//获取选中的数据
quartzServerManager.getSelectData = function(){
	$("#task-list").datagrid({
		onSelect : function (index, row){
			var rowStr = JSON.stringify(row);			
			var nums = $.inArray(rowStr,quartzServerManager.selectRowData);
            if(nums == -1)
			    quartzServerManager.selectRowData.push(rowStr);	
            
            console.log( quartzServerManager.selectRowData);
												
		},
		onUnselect: function(index, row){
			var rowStr = JSON.stringify(row);
			var nums = $.inArray(rowStr,quartzServerManager.selectRowData);
			if(nums > -1)
				quartzServerManager.selectRowData.splice(nums,1);		
		}
	});
}

//通过复选框选择数据
quartzServerManager.getCheckData = function(){
	quartzServerManager.selectRowData = [];
	$("#task-list").datagrid({
		/*onCheck: function(index,row){
			var rowStr = JSON.stringify(row);			
			var nums = $.inArray(rowStr,quartzServerManager.selectRowData);
            if(nums == -1)
			    quartzServerManager.selectRowData.push(rowStr);	
		},*/
		onCheckAll : function(rows){			
			quartzServerManager.selectRowData = [];
			$.each(rows,function(index,item){
				quartzServerManager.selectRowData.push(JSON.stringify(item));
			});
		},
	    onUncheckAll : function(rows){
	    	quartzServerManager.selectRowData = [];
	    }/*,
	    onUncheck : function(index, row){
	    	var rowStr = JSON.stringify(row);
	    	//var nums = quartzServerManager.selectRowData.indexOf(row);
	    	var nums = $.inArray(rowStr,quartzServerManager.selectRowData);
			if(nums > -1)
				quartzServerManager.selectRowData.splice(nums,1);
	    }*/
	    
	});
}


//编缉任务信息
quartzServerManager.editSelectJob = function(){	
	var rows = quartzServerManager.selectRowData.length;
	if(rows != 1 ){
		$.messager.alert('提示','请选择一行数据进行编缉,当前选中 '+ quartzServerManager.selectRowData.length +' 条数据！');
		return;
	}
	
	$("#dd").css("display","block");
	$("#jobGroup,#jobName").textbox('readonly',true);
	
	$("#dd").dialog({
		title: '编辑任务',
		width: 500,
		height: 400,
		closed: false,
		cache: false,
		modal: true,
		buttons:[{
			text:'确定',
			iconCls:'icon-ok',
			handler:function(){
				var jobGroup = $("#jobGroup").textbox("getValue");
				var jobName = $("#jobName").textbox("getValue");
				var jobDescription = $("#jobDescription").val();
				var jobClassName = quartzServerManager.selectRowData[0].jobClassName;
				var str= {
					"jobGroup":jobGroup,
					"jobName":jobName,
					"jobDescription":jobDescription,
					"jobClassName":jobClassName
				};
				
				$.ajax({
		            type: 'POST',
		            url: top.Client.CONST_PATH + "/quartzServerManager/editJobInfo",	            
					data: JSON.stringify(str),
		            dataType:"json"	,				
					contentType:"application/json;charset=utf-8",		
		            success: function(data) {
		            	$('#dd').dialog('close');
		                if(data){
		                	quartzServerManager.selectRowData = [];
		                	$.messager.alert('提示','更新任务信息成功');
		                	$("#task-list").datagrid('reload');
		                }
		            }
		        });
			}
		},{
			text:'取消',
			iconCls:'icon-cancel',
			handler:function(){
				$('#dd').dialog('close');
			}
		}]
	});
	
	$("#jobGroup").textbox('setValue',quartzServerManager.selectRowData[0].jobGroup);
	$("#jobName").textbox('setValue',quartzServerManager.selectRowData[0].jobName);
	$("#jobDescription").val(quartzServerManager.selectRowData[0].jobDescription);		
}

//添加任务
quartzServerManager.addJob = function(){
	$("#dd").css("display","block");
	$("#jobGroup,#jobName").textbox('readonly',false);
	$("#dd").dialog({
		title: '添加任务',
		width: 500,
		height: 400,
		closed: false,
		cache: false,
		modal: true,
		buttons:[{
			text:'确定',
			iconCls:'icon-ok',
			handler:function(){
				var jobGroup = $("#jobGroup").textbox("getValue");
				var jobName = $("#jobName").textbox("getValue");
				var jobDescription = $("#jobDescription").val();
				var jobClassName = "com.papi.quartz.quartzjobs.BasicJob";
				
				var str= {
					"jobGroup":jobGroup,
					"jobName":jobName,
					"jobDescription":jobDescription,
					"jobClassName":jobClassName
				};
				
				$.ajax({
		            type: 'POST',
		            url: top.Client.CONST_PATH + "/quartzServerManager/editJobInfo",	            
					data: JSON.stringify(str),
		            dataType:"json"	,				
					contentType:"application/json;charset=utf-8",		
		            success: function(data) {
		            	$('#dd').dialog('close');
		                if(data){
		                	quartzServerManager.selectRowData = [];
		                	$.messager.alert('提示','添加任务成功');
		                	$("#task-list").datagrid('reload');
		                }
		            }
		        });
			}
		},{
			text:'取消',
			iconCls:'icon-cancel',
			handler:function(){
				$('#dd').dialog('close');
			}
		}]
	});
	$("#jobGroup,#jobName").textbox('setValue',"");
	$("#jobDescription,#repeatIntervalUnit").val("");	
	$('#startDateTime,#endDateTime').datetimebox("setValue",quartzServerManager.getnowtime());
	
	$("#isRepeatTrigger").attr("checked",false);
	$('#cronTriggerSetDiv').css("display","none");
	$('#repeatCount,#repeatInterval').numberbox("setValue","");
	$("#repeatIntervalUnit").combobox("setValue","");
}
 

//删除任务
quartzServerManager.deleteJob = function(){	
	$.each(quartzServerManager.selectRowData,function(index,item){
		quartzServerManager.selectRowData[index] = JSON.parse(item);
	});
	
	var rows = quartzServerManager.selectRowData.length;
	if(rows >0 ){
		$.messager.confirm('提示','当前选中 '+ quartzServerManager.selectRowData.length +' 条数据！是否确定删除？',function(r){
			if(r){
				quartzServerManager.onloading();
				
				$.ajax({
		            type: 'POST',
		            url: top.Client.CONST_PATH + "/quartzServerManager/deleteJobs",	            
					data: JSON.stringify(quartzServerManager.selectRowData),
		            dataType:"json"	,				
					contentType:"application/json;charset=utf-8",		
		            success: function(data) {
		                if(data){
		                	quartzServerManager.removeload();
		                	
		                	quartzServerManager.selectRowData = [];
		                	$.messager.alert('提示','删除任务成功');
		                	$("#task-list").datagrid('reload');
		                }
		            }
		        });
			}else{
				return;
			}
			
		});		
	}
}

//暂停任务
quartzServerManager.pauseJobs = function(){
	$.each(quartzServerManager.selectRowData,function(index,item){
		quartzServerManager.selectRowData[index] = JSON.parse(item);
	});
	
	var rows = quartzServerManager.selectRowData.length;
	var falg = true;
	
	$.each(quartzServerManager.selectRowData,function(index,item){
		//quartzServerManager.selectRowData[index] = JSON.parse(item);
		if(item.status == "暂停"){
			$.messager.alert('提示','请不要选中已经暂停的任务');
			
			quartzServerManager.selectRowData = [];        	
        	$("#task-list").datagrid('reload');
			
			falg = false;
			return false;
		}
	});
	
	if(rows >0 && falg){				
		$.messager.confirm('提示','当前选中 '+ quartzServerManager.selectRowData.length +' 条数据！是否确定暂停所选的任务？',function(r){
			if(r){
				$.ajax({
		            type: 'POST',
		            url: top.Client.CONST_PATH + "/quartzServerManager/pauseJobs",	            
					data: JSON.stringify(quartzServerManager.selectRowData),
		            dataType:"json"	,				
					contentType:"application/json;charset=utf-8",		
		            success: function(data) {
		                if(data){
		                	quartzServerManager.selectRowData = [];
		                	$.messager.alert('提示','暂停任务成功');
		                	$("#task-list").datagrid('reload');
		                }
		            }
		        });
			}else{
				return;
			}
			
		});		
	}	
}

//恢复任务
quartzServerManager.resumeJobs = function(){
	$.each(quartzServerManager.selectRowData,function(index,item){
		quartzServerManager.selectRowData[index] = JSON.parse(item);
	});
	
	var rows = quartzServerManager.selectRowData.length;
	var falg = true;
	
	$.each(quartzServerManager.selectRowData,function(index,item){
		//quartzServerManager.selectRowData[index] = JSON.parse(item);
		if(item.status == "正常"){
			$.messager.alert('提示','请不要选中已经正常运行的任务');
			
			quartzServerManager.selectRowData = [];        	
        	$("#task-list").datagrid('reload');
			
			falg = false;
			return false;
		}
	});
	
	if(rows >0 && falg){
		$.messager.confirm('提示','当前选中 '+ quartzServerManager.selectRowData.length +' 条数据！是否确定恢复所选的任务？',function(r){
			if(r){
				$.ajax({
		            type: 'POST',
		            url: top.Client.CONST_PATH + "/quartzServerManager/resumeJobs",	            
					data: JSON.stringify(quartzServerManager.selectRowData),
		            dataType:"json"	,				
					contentType:"application/json;charset=utf-8",		
		            success: function(data) {
		                if(data){
		                	quartzServerManager.selectRowData = [];
		                	$.messager.alert('提示','恢复任务成功');
		                	$("#task-list").datagrid('reload');
		                }
		            }
		        });
			}else{
				return;
			}
			
		});		
	}
}

//批量添加任务
quartzServerManager.batchAddJobs =  function(){
	$("#batchAddJobsDiv").css("display","block");	
	$("#batchAddJobsDiv").dialog({
		title: '批量添加任务',
		width: 450,
		height: 250,
		closed: false,
		cache: false,
		modal: true,
		buttons:[{
			text:'确定',
			iconCls:'icon-ok',
			handler:function(){
				var jobCnt = $("#jobCnt").numberbox("getValue");
				var jobClassName = "com.papi.quartz.quartzjobs.BasicJob";
				
				var str= {
					"jobCnt":jobCnt,
					"jobClassName":jobClassName
				};
				
				$('#batchAddJobsDiv').dialog('close');
				quartzServerManager.onloading();
				
				$.ajax({
		            type: 'POST',
		            url: top.Client.CONST_PATH + "/quartzServerManager/batchAddJobs",	            
					data: JSON.stringify(str),
		            dataType:"json"	,				
					contentType:"application/json;charset=utf-8",		
		            success: function(data) {		            	
		                if(data){
		                	quartzServerManager.selectRowData = [];
		                	quartzServerManager.removeload();
		                	$.messager.alert('提示','批量添加任务成功');
		                	$("#task-list").datagrid('reload');
		                }
		            }
		        });
								
			}
		},{
			text:'取消',
			iconCls:'icon-cancel',
			handler:function(){
				$('#batchAddJobsDiv').dialog('close');
			}
		}]
	});
}

//设置选择重复触发器后的文本样式
quartzServerManager.onClickEvent= function(){
	$('#isRepeatTrigger').click(function(){		
		var flag = $('#isRepeatTrigger').is(':checked');
		if(flag == true){
			$('#repeatCount,#repeatInterval').numberbox({
				required : true,
				min:1
			});
			
			$('#repeatIntervalUnit').combobox({
				required : true
			});
		}else{
			$('#repeatCount,#repeatInterval').numberbox({
				required : false
			});
			
			$('#repeatIntervalUnit').combobox({
				required : false
			});
		}
	})
}

//quartzServerManager
quartzServerManager.listenTriggerType = function(){
	$("#triggerType").combobox({
		onChange : function(){
			var tirggerType = $("#triggerType").combobox('getValue');
			if(tirggerType == 'cronTrigger'){
				$('#cronTriggerSetDiv').css("display","block");
				$('#triggerSetDiv').css("display","none");
			}else{
				$('#cronTriggerSetDiv').css("display","none");
				$('#triggerSetDiv').css("display","block");
			}
		}
	});

}

quartzServerManager.onloading = function(){  
    $("<div class=\"datagrid-mask\"></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo("body");   
    $("<div class=\"datagrid-mask-msg\"></div>").html("正在处理，请稍候。。。").appendTo("body").css({display:"block","z-index":"200000",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2});   
}  
quartzServerManager.removeload = function(){  
   $(".datagrid-mask").remove();  
   $(".datagrid-mask-msg").remove();  
}  

quartzServerManager.getnowtime = function(){
    var nowtime = new Date();
    var year = nowtime.getFullYear();
    var month = quartzServerManager.padleft0(nowtime.getMonth() + 1);
    var day = quartzServerManager.padleft0(nowtime.getDate());
    var hour = quartzServerManager.padleft0(nowtime.getHours());
    var minute = quartzServerManager.padleft0(nowtime.getMinutes()+1);
    var second = quartzServerManager.padleft0(nowtime.getSeconds());
   
    return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second ;
}
//补齐两位数
quartzServerManager.padleft0 = function(obj) {
    return obj.toString().replace(/^[0-9]{1}$/, "0" + obj);
}