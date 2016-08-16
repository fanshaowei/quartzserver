<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@include file="../../common/common.jsp"%>
<title>任务列表</title>
<style type="text/css">
    html,body{
        padding : 0;
        margin: 0;
        height:100%;
        width:100%;
    }
   .contain-title{
      display: inline-block;
      height:100%;     
    }
   .contain-title-memo{
       display:inline-block;   
       position:absolute;    
       width: 100%;
       line-height: 38px;
       font-size: 14px;
       text-align:center;
   }
   .dd{
       position: relative;
    }
   .dd form .easyui-textbox{
        width: 150px;        
    }
    .dd form textarea{
        vertical-align: middle;
        width: 280px;
        height: 100px;
        border-color: #95B8E7;
    }
    .dd form > div{
        margin-top: 15px;
        margin-left:15px;
    }
    
    .triggerSetDiv > div{
    	margin-top: 10px;
    }
    
    
    #batchAddJobFrom .easyui-numberbox{
        width: 100px;
    }
    #batchAddJobFrom > div{
        margin-top: 15px;
        margin-left:15px;
    }
</style>
<script type="text/javascript" src="./js/quartzServerManager.js"></script>
<script type="text/javascript">
	
</script>
</head>
<body>
    <div style="height:4%;width:100%;background:#bdc3c7;">
        <span class="contain-title" >
	        <!-- <span class="contain-title-icon icon-configure" ></span> -->
	        <span class="contain-title-memo">定时任务测试</span>
        </span>               
    </div>
    
    <div style="height:96%;width:100%;position:absolute;">
	<table id="task-list" style="height:100%;width:100%;">
	    <thead>
		    <tr>
		      <th data-options="field:'ck',checkbox:true"></th>
		      <th data-options="field:'jobGroup' ,align:'center'"  width="80">任务组</th>
		      <th data-options="field:'jobName' ,align:'center'" width="80">任务名</th>		      
		      <th data-options="field:'status' ,align:'center'" width="80">任务状态</th>
		      <th data-options="field:'jobDescription' ,align:'center'" width="80">备注</th>
		      <th data-options="field:'jobClassName' ,align:'center'" width="80">执行任务相关类</th>	      
		    </tr>
		</thead>	    
    </table>
    </div>
    
    <div id="dd" class="dd" style="display:none;">
         <form id="ff">
             <div>
                 <label for="jobGroup">任务组:</label>
                 <input id="jobGroup" class="easyui-textbox" type="text" name="jobGroup" data-options="required:true" />
                 
                 <label for="jobName">任务名:</label>
		        <input id="jobName" class="easyui-textbox" type="text" name="jobName"  data-options="required:true" />                 
             </div>
             
             <div>
                 <label>触发器类型:</label>
                 <select id="triggerType" class="easyui-combobox" name="triggerType" data-options="required:true" panelHeight="100" style="width:150px;">
			        <option value="simpleTrigger">简单触发器</option>			        
			        <option value="dailyTrigger">每天间隔触发器</option>
			        <option value="cronTrigger">cron触发器</option>				        
			     </select>
             </div>
             
             <div id="triggerSetDiv" class="triggerSetDiv">
	             <div>
	                 <div>
	                     <label>开始时间:</label>
	                     <input id='startDateTime' class="easyui-datetimebox" editable="false" name="startDateTime" data-options="required:true" style="width:150px" />
	                     
	                     <label>结束时间:</label>
	                     <input id='endDateTime' class="easyui-datetimebox" name="endDateTime" editable="false" data-options="required:true" style="width:150px" />
	                 </div>                 
	             </div>
	             <div>
	                  <label>是否重复:</label>
	                  <input id="isRepeatTrigger" name="isRepeatTrigger" type="checkbox" style="vertical-align: middle;" />
	                  
	                  <label style="margin-left:100px;">重复次数:</label>
	                  <input id="repeatCount" name="repeatCount"  type="text" class="easyui-numberbox"  data-options="min:1" style="width:100px;" />
	             </div>
	             <div>
	                  <label>重复间隔:</label>
	                  <input id="repeatInterval" name="repeatInterval" type="text" class="easyui-numberbox"  data-options="min:1" style="width:100px;" />                  
	                  
	                  <label style="margin-left:20px;">重复单位:</label>
	                  <select id="repeatIntervalUnit" class="easyui-combobox" name="repeatIntervalUnit"  panelHeight="100" style="width:100px;">
				        <option value="SECOND">秒</option>
				        <option value="MINUTE">分</option>
				        <option value="HOUR">时</option>				        
				     </select>
	             </div>
		     </div>
		     
		     <div id="cronTriggerSetDiv" class="cronTriggerSetDiv">
		         <label>cron表达式:</label>
		         <input id="cronExpress" name="cronExpress" type="text" class="easyui-textbox" data-options="required:true" />
		     </div>
		     
		     <div>
		         <label for="jobDescription">备  注:</label>
		         <textarea id="jobDescription" name="jobDescription"></textarea>
		     </div>
         </form>
    </div>
    
    <div id="batchAddJobsDiv" class="batchAddJobsDiv" style="display:none;">
        <form id="batchAddJobFrom">
            <div>
                 <label for="jobCnt">任务数:</label>
                 <input id="jobCnt" class="easyui-numberbox" type="text" name="jobCnt" data-options="required:true" />              
            </div>		     
        </form>
    </div>
</body>
</html>