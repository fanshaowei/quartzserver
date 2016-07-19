<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="../../common/common.jsp"%>
<title>任务列表</title>
<style type="text/css">
    html,body{
        padding : 0;
        margin: 0;
    }
</style>
<script type="text/javascript">
	$(document).ready(function(){		
		$("#task-list").datagrid({
			width:"500",
			height:"600",
			//url:top.Client.CONST_PATH + "/job/goList",
			fitColumns:true,
			fit:true,	
			rownumbers:true,
			toolbar:[{
				iconCls: "icon-edit",
				text:"编辑",
				handler: function(){
					
				}
			},'-',{iconCls: "icon-add",
				   text:"添加",
				   handler: function(){
					   
				   }
			},'-',{iconCls: "icon-no",
				   text:"删除",
				   handler: function(){
					   
				   }
			}],
			loadMsg:'loading...'
		});
		 		
		 $.ajax({
			type:"get",
			async:false,
			dataType:"json",
			url:top.Client.CONST_PATH + "/job/goList",
			success:function(data){				
				var jsonArray = [];
				$.each(data.jobInfos, function(i,val){																  
				    jsonArray[i] = val;
				});	
				
				$("#task-list").datagrid({data : jsonArray});						
			}
		});
		
		
	})
   
</script>
</head>
<body>
    <div style="height:89%;width:100%;position:absolute;">
	<table id="task-list" style="height:100%;width:100%;">
	    <thead>
		    <tr>
		      <th data-options="field:'jobName' ,align:'center'" width="80">任务名</th>
		      <th data-options="field:'jobGroup' ,align:'center'"  width="80">任务组</th>
		      <th data-options="field:'cronExpr' ,align:'center'" width="80">时间表达式</th>
		      <th data-options="field:'jobStatus' ,align:'center'" width="80">状态</th>
		      <th data-options="field:'jobMemo' ,align:'center'" width="80">备注</th>
		      <th data-options="field:'option' ,align:'center'" width="80">操作</th>		      
		    </tr>
		</thead>	    
    </table>
    </div>
</body>
</html>