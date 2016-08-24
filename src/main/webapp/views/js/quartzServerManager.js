var quartzServerManager = {
	selectRowData:[]
};

$(document).ready(function(){		
	//初始化管理列表
	quartzServerManager.initDataGrid();
	//获取选中的数据
	quartzServerManager.getSelectData();
	quartzServerManager.getCheckData();
	
	quartzServerManager.listenTriggerType("add");
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
		},'-',{
			iconCls: "icon-recordlist",
			text:"查看任务执行日志",
			handler: function(){
				quartzServerManager.getJobLogs();
			}
		}],
		method:"get",
		url: top.Client.CONST_PATH + "/quartzServerManager/getAllJobDetails",
		loadMsg:'数据加载中...'
	});
	 		
}

//初始化表单元素
quartzServerManager.initFormElements = function(){
	$("#jobGroup,#jobName").textbox('setValue',"");
	$("#jobDescription").val("");
	
	$('#triggerType').combobox({		
		editable:false
	});
	$('#triggerType').combobox('setValue','SIMPLE_TRIGGER');
	
	$('#dayOfWeek').combobox({
		width:355,
		panelHeight:200,
		editable:false,
		multiple:true
	});
	
	$('#startDateTime,#endDateTime').datetimebox({
		width:150,
		required:true,
		editable:false
	});	
	
	$('#startDateTime').datetimebox("setValue",quartzServerManager.getnowtime("start"));
	$('#endDateTime').datetimebox("setValue",quartzServerManager.getnowtime("end"));
	$("#isRepeatTrigger").attr("checked",false);	
	$('#repeatCount,#repeatInterval').numberbox("setValue","1");
	$('#repeatIntervalUnit').combobox("setValue","SECOND");
	
	$('#cronTriggerSetDiv').css("display","none");
	$('#cronExpress').textbox("setValue","");
	$('#dayOfWeekDiv').css("display","none");
	$("#endDateTimeDiv").css("display","none");
}

//获取表单元素的值
quartzServerManager.getFormElementsVal = function(){
	var jobGroup = $("#jobGroup").textbox("getValue");
	var jobName = $("#jobName").textbox("getValue");
	var jobDescription = $("#jobDescription").val();
	var jobClassName = "com.papi.quartz.quartzjobs.HelloJob";
	
	var triggerType = $("#triggerType").combobox("getValue");
	
	var startDateTime = "", endDateTime = "";
	if(triggerType == "SIMPLE_TRIGGER"){
	    startDateTime = $("#startDateTime").datetimebox("getValue");
		endDateTime = $("#endDateTime").datetimebox("getValue");
	}else if(triggerType == "DAILY_TRIGGER"){
		startDateTime = $("#startDateTime").timespinner("getValue");
		endDateTime = $("#endDateTime").timespinner("getValue");
		//$("#endDateTimeDiv").css("display","none");
	}	
	
	var dayOfWeek = $("#dayOfWeek").combobox("getValues"); 
	var isRepeatTrigger = $('#isRepeatTrigger').is(':checked');
	var repeatCount = $('#repeatCount').numberbox('getValue');
	var repeatInterval = $('#repeatInterval').numberbox('getValue');
	var repeatIntervalUnit = $('#repeatIntervalUnit').combobox('getValue');
	var cronExpress = $('#cronExpress').textbox('getValue');				
	
	var str= {
		"jobGroup":jobGroup,
		"jobName":jobName,
		"jobDescription":jobDescription,
		"jobClassName":jobClassName,
		
		"triggerType":triggerType,
		"startDateTime":startDateTime,
		"endDateTime":endDateTime,
		"dayOfWeek" : dayOfWeek,
		"isRepeatTrigger":isRepeatTrigger,
		"repeatCount":repeatCount,
		"repeatInterval":repeatInterval,
		"repeatIntervalUnit":repeatIntervalUnit,
		"cronExpress":cronExpress					
	};
	
	return str;
}

//添加任务
quartzServerManager.addJob = function(){
	$("#dd").css("display","block");
	$("#jobGroup,#jobName").textbox('readonly',false);
	
	$("#dd").dialog({
		title: '添加任务',
		width: 500,
		height: 450,
		closed: false,
		cache: false,
		modal: true,
		buttons:[{
			text:'确定',
			iconCls:'icon-ok',
			handler:function(){								
				var str = quartzServerManager.getFormElementsVal();
				if(!quartzServerManager.verifyElements()){
					return false;
				}
				
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
				quartzServerManager.selectRowData = [];
				$('#dd').dialog('close');
			}
		}]
	});
	
	quartzServerManager.initFormElements();
	quartzServerManager.triggerTypeChangeToShow('add','SIMPLE_TRIGGER');

}

//设置编辑表单
quartzServerManager.setEditFormVals = function(){
	var editData = JSON.parse(quartzServerManager.selectRowData[0]),
    jobName = editData.jobName,
	jobGroup = editData.jobGroup,
	jobDescription = editData.jobDescription;	  
	
	if(editData.triggerInfoList == null ){
		quartzServerManager.initFormElements();
		quartzServerManager.triggerTypeChangeToShow('edit','SIMPLE_TRIGGER');
	}else{
		var triggerType = editData.triggerInfoList.triggerType, 
		cronExpression = editData.triggerInfoList.cronExpression,
	    simpleStartDateStr = editData.triggerInfoList.simpleStartDateStr,
	    simpleEndDateStr = editData.triggerInfoList.simpleEndDateStr,
	    startTimeOfDay = editData.triggerInfoList.startTimeOfDay,
	    endTimeOfDay = editData.triggerInfoList.endTimeOfDay,	    
	    isRepeatTrigger = editData.triggerInfoList.isRepeatTrigger,
	    repeatCount = editData.triggerInfoList.repeatCount,
	    repeatInterval = editData.triggerInfoList.repeatInterval,
	    repeatIntervalUnit = editData.triggerInfoList.repeatIntervalUnit,
	    dayOfWeekArr = editData.triggerInfoList.dayOfWeek;
	   			
		$('#triggerType').combobox('setValue',triggerType);
		
		quartzServerManager.triggerTypeChangeToShow('edit',triggerType);
		
		if(triggerType == 'SIMPLE_TRIGGER'){		
			$('#startDateTime').datetimebox('setValue',simpleStartDateStr);
			$('#endDateTime').datetimebox('setValue',simpleEndDateStr);		
		}else if(triggerType == 'DAILY_TRIGGER'){
			$('#startDateTime').timespinner('setValue',startTimeOfDay);
			$('#endDateTime').timespinner('setValue',"23:59:59");
			//$("#endDateTimeDiv").css("display","none");
			$('#dayOfWeek').combobox('setValues',dayOfWeekArr);
		}	
		if(isRepeatTrigger){
			document.getElementById("isRepeatTrigger").checked = true;			
		}else{
			document.getElementById("isRepeatTrigger").checked = false;
		}
		
		$("#repeatCount").numberbox("setValue",repeatCount);
		$("#repeatInterval").numberbox("setValue",repeatInterval);
		$("#repeatIntervalUnit").combobox("setValue",repeatIntervalUnit);
		$("#cronExpress").textbox("setValue",cronExpression);
	}
	
	$("#jobGroup").textbox('setValue', jobGroup);
	$("#jobName").textbox('setValue', jobName);
	$("#jobDescription").val(jobDescription);
	   
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
		height: 450,
		closed: false,
		cache: false,
		modal: true,
		buttons:[{
			text:'确定',
			iconCls:'icon-ok',
			handler:function(){				
				var str = quartzServerManager.getFormElementsVal();
				if(!quartzServerManager.verifyElements()){
					return false;
				}
				
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
				quartzServerManager.selectRowData = [];
				$('#dd').dialog('close');
			}
		}]
	});
	
	quartzServerManager.initFormElements();
	quartzServerManager.setEditFormVals();	
			
}
 
//获取选中的数据
quartzServerManager.getSelectData = function(){
	$("#task-list").datagrid({
		onSelect : function (index, row){
			var rowStr = JSON.stringify(row);			
			var nums = $.inArray(rowStr,quartzServerManager.selectRowData);
            if(nums == -1)
			    quartzServerManager.selectRowData.push(rowStr);											
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
		onCheckAll : function(rows){			
			quartzServerManager.selectRowData = [];
			$.each(rows,function(index,item){
				quartzServerManager.selectRowData.push(JSON.stringify(item));
			});
		},
	    onUncheckAll : function(rows){
	    	quartzServerManager.selectRowData = [];
	    }
	    
	});
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
				var jobClassName = "com.papi.quartz.quartzjobs.HelloJob";
				
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
				required : false,
				min:1,
				value:1
			});
			
			$('#repeatIntervalUnit').combobox({
				required : false,
			});
		}
	});
}

//根据选择框的值 改变监听其他元素
quartzServerManager.listenTriggerType = function(operateType){		
	$("#triggerType").combobox({
		onChange : function(){
			var triggerType = $("#triggerType").combobox('getValue');
			quartzServerManager.triggerTypeChangeToShow(operateType,triggerType);
		}
	});
	
	$('#dayOfWeek').combobox({
		onSelect : function(record){
			var valuesArray = $('#dayOfWeek').combobox("getValues");
			var recordValue = record.value;
			
			if(recordValue == 'everyDay'){
				$.each(valuesArray,function(index,item){
					if(item != 'everyDay')
						$('#dayOfWeek').combobox('unselect',item);
				});
			}else{
				$.each(valuesArray,function(index,item){
					if(item == 'everyDay')
						$('#dayOfWeek').combobox('unselect',item);
				});
			}		
		}
	}); 
		
}

quartzServerManager.triggerTypeChangeToShow = function(operateType,triggerType){
	if(triggerType == 'CRON_TRIGGER'){
		$('#cronTriggerSetDiv').css("display","block");
		$('#triggerSetDiv,#dayOfWeekDiv').css("display","none");
	}else if(triggerType == 'SIMPLE_TRIGGER'){
		$('#cronTriggerSetDiv,#dayOfWeekDiv').css("display","none");
		$('#triggerSetDiv').css("display","block");
		
		$('#startDateTime,#endDateTime').datetimebox({
			width:150,
			required:true,
			editable:false
		});					
		if(operateType == "add"){
			$('#startDateTime').datetimebox("setValue",quartzServerManager.getnowtime("start"));
			$('#endDateTime').datetimebox("setValue",quartzServerManager.getnowtime("end"));	
		}
		
	}else if(triggerType == 'DAILY_TRIGGER'){
		$('#cronTriggerSetDiv').css("display","none");
		$('#triggerSetDiv,#dayOfWeekDiv').css("display","block");	
		//$("#endDateTimeDiv").css("display","none");
		
		$('#startDateTime,#endDateTime').timespinner({
			width:150,
			required: true,
		    showSeconds: true
		});
		if(operateType == "add"){
			$('#startDateTime').timespinner("setValue",quartzServerManager.getnowtime("start").substr(11));
			$('#endDateTime').timespinner("setValue","23:59:59");
		}
	}
}
	

quartzServerManager.verifyElements = function(){
	var triggerType = $('#triggerType').combobox('getValue');
	if(triggerType == "SIMPLE_TRIGGER"){
		var startDateTime = $('#startDateTime').datetimebox("getValue");
		if(new Date(startDateTime) > new Date(quartzServerManager.getnowtime(""))){
			return true;
		}
	}else if(triggerType == "DAILY_TRIGGER" ){
		var startDateTime = $('#startDateTime').timespinner("getValue");
		startDateTime = quartzServerManager.getnowtime().substring(0,11) + startDateTime;
	    
		if(new Date(startDateTime) > new Date(quartzServerManager.getnowtime(""))){
			return true;
		}
	}
	
	$.messager.alert('提示','开始时间不能小于当前时间');
	
	return false;
} 


//遮罩、加载
quartzServerManager.onloading = function(){  
    $("<div class=\"datagrid-mask\"></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo("body");   
    $("<div class=\"datagrid-mask-msg\"></div>").html("正在处理，请稍候。。。").appendTo("body").css({display:"block","z-index":"200000",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2});   
}  
quartzServerManager.removeload = function(){  
   $(".datagrid-mask").remove();  
   $(".datagrid-mask-msg").remove();  
}  

//获取当前时间
quartzServerManager.getnowtime = function(flag){
    var nowtime = new Date();
    var year = nowtime.getFullYear();
    var month = quartzServerManager.padleft0(nowtime.getMonth() + 1);
    var day = quartzServerManager.padleft0(nowtime.getDate());
    var hour = quartzServerManager.padleft0(nowtime.getHours());
    var minute;
    if(flag == "start"){
    	 minute = quartzServerManager.padleft0(nowtime.getMinutes()+1);
    }else if(flag == "end"){
    	 minute = quartzServerManager.padleft0(nowtime.getMinutes()+2);	
    }else{
    	minute = quartzServerManager.padleft0(nowtime.getMinutes());
    }    	
    
    var second = quartzServerManager.padleft0(nowtime.getSeconds());
   
    return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second ;
}
//补齐两位数
quartzServerManager.padleft0 = function(obj) {
    return obj.toString().replace(/^[0-9]{1}$/, "0" + obj);
}

quartzServerManager.format = function(time, format){
	    var t = new Date(time);
	    var tf = function(i){return (i < 10 ? '0' : '') + i};
	    return format.replace(/yyyy|MM|dd|HH|mm|ss/g, function(a){
	        switch(a){
	            case 'yyyy':
	                return tf(t.getFullYear());
	                break;
	            case 'MM':
	                return tf(t.getMonth() + 1);
	                break;
	            case 'mm':
	                return tf(t.getMinutes());
	                break;
	            case 'dd':
	                return tf(t.getDate());
	                break;
	            case 'HH':
	                return tf(t.getHours());
	                break;
	            case 'ss':
	                return tf(t.getSeconds());
	                break;
	        }
	    })
	}

//获取任务的执行日志
quartzServerManager.getJobLogs = function(){
	var rows = quartzServerManager.selectRowData.length;		
	if(rows != 1 ){
		$.messager.alert('提示','请选择一条任务查看相关日志,当前选中 '+ quartzServerManager.selectRowData.length +' 条数据！');
		return false;
	}
	
	var selectData = JSON.parse(quartzServerManager.selectRowData[0]);
	
/*	$.ajax({
        type: 'GET',
        dataType: 'json',
        url: top.Client.CONST_PATH + "/quartzServerManager/getJobLogs?jobName=" + selectData.jobName + "&jobGroup=" + selectData.jobGroup,	            						
        success: function(data) {		            	
            console.log(data);
        }
    });*/
	
	$("#jobLog-list").datagrid({
		width:700,
		height:850,
		fitColumns:true,
		fit:true,	
		rownumbers:true,
		singleSelect:false,
		method:"get",
		url: top.Client.CONST_PATH + "/quartzServerManager/getJobLogs?jobName=" + selectData.jobName + "&jobGroup=" + selectData.jobGroup,
		loadMsg:'数据加载中...'			
	});
	
	$("#jobLogDiv").css("display","block");
	$("#jobLogDiv").dialog({
		title: '任务日志',
		width: 950,
		height: 900,
		closed: false,
		cache: false,
		modal: true
	});
}

quartzServerManager.setColoumsFormater = function(val){
	if(val == "SIMPLE_TRIGGER")
		return "简单触发器";
	else if (val == "DAILY_TRIGGER")
		return "每天间隔触发器";
	else if (val == "CRON_TRIGGER")
		return "cron触发器";
}
