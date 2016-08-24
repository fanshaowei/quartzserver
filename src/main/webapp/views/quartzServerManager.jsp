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
    
    body,td,th,div,input,a,lable {
	font-family: "微软雅黑", "宋体", "Arial",  "Verdana", "sans-serif";
	font-size: 14px;
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
    <div style="height:5%;width:100%;">
        <span class="contain-title" >
	        <!-- <span class="contain-title-icon icon-configure" ></span> -->
	        <span class="contain-title-memo"><h3>定时服务器测试</h3></span>
        </span>               
    </div>
    
    <div style="height:95%;width:100%;position:absolute;">
	<table id="task-list" style="height:100%;width:100%;">
	    <thead>
		    <tr>
		      <th data-options="field:'ck',checkbox:true"></th>
		      <th data-options="field:'jobGroup' ,align:'center'"  width="80">任务组</th>
		      <th data-options="field:'jobName' ,align:'center'" width="80">任务名</th>		      
		      <th data-options="field:'status' ,align:'center'" width="80">任务状态</th>		      
		      <th data-options="field:'jobClassName' ,align:'center'" width="80" hidden="true">执行任务相关类</th>		      
		      <th data-options="field:'triggerType' ,align:'center', formatter:function(value){return quartzServerManager.setColoumsFormater(value);}" width="80">触发器类型</th>
		      <th data-options="field:'fireDate' ,align:'center'" width="80">最近触发时间</th>		  
		      <th data-options="field:'nextFireDate' ,align:'center'" width="80">下次触发时间</th>		      	     
		      <th data-options="field:'triggerInfoList' ,align:'center'" width="80" hidden="true">触发器详细信息</th>
		      <th data-options="field:'jobDescription' ,align:'center'" width="80">备注</th>
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
			        <option value="SIMPLE_TRIGGER">简单触发器</option>			        
			        <option value="DAILY_TRIGGER">每天间隔触发器</option>
			        <option value="CRON_TRIGGER">cron触发器</option>				        
			     </select>
             </div>
             
             <div id="triggerSetDiv" class="triggerSetDiv">
                 <div id="dayOfWeekDiv">
                     <label>指定日期:</label>
                     <select id="dayOfWeek" name="dayOfWeek" class="easyui-combobox">
				        <option value="everyDay">全部</option>
				        <option value="MON">星期一</option>
				        <option value="TUE">星期二</option>
				        <option value="WED">星期三</option>	
				        <option value="THU">星期四</option>
				        <option value="FRI">星期五</option>
				        <option value="SAT">星期六</option>
				        <option value="SUN">星期日</option>			        
				     </select>
                 </div>
             
	             <div>
	                 <div>
	                     <label>开始时间:</label> <!-- class="easyui-datetimebox"  data-options="required:true"  editable="false" style="width:150px"-->
	                     <input id='startDateTime'  name="startDateTime" />
	                     
	                     <div id="endDateTimeDiv">
	                     <label>结束时间:</label>
	                     <input id='endDateTime'  name="endDateTime" />
	                     </div>
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
    
    <div id="jobLogDiv" style="height:96%;width:100%;display:none;">
        <table id="jobLog-list" style="height:100%;width:100%;">
	    <thead>
		    <tr>
		      <th data-options="field:'jobGroup' ,align:'center'"  width="80">任务组</th>
		      <th data-options="field:'jobName' ,align:'center'" width="80">任务名</th>		      		      	     
		      <th data-options="field:'fireDate' ,align:'center' , formatter:function(value){return quartzServerManager.format(value, 'yyyy-MM-dd HH:mm:ss');}" width="80" >执行时间</th>		      
		      <th data-options="field:'nextFireDate' ,align:'center' , formatter:function(value){return quartzServerManager.format(value, 'yyyy-MM-dd HH:mm:ss');}" width="80">下次执行时间</th>		
		      <th data-options="field:'jobStatus' ,align:'center'" width="80">执行状态</th>
		      <th data-options="field:'firedResult' ,align:'center'" width="80">执行结果</th>		      
		    </tr>
		</thead>	    
        </table>
    </div>
</body>
</html>