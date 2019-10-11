package com.activity.learn;

import static org.junit.Assert.assertTrue;

import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Unit test for simple App.
 */

public class ActivityDemoTest {

    private Logger logger = LoggerFactory.getLogger(ActivityDemoTest.class);

    private static ProcessEngine processEngine;

    /**
     * 初始化24张原始表
     */
    @BeforeClass
    public static void init() {
        processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
    }

    /**
     * empty test，只为了使@BeforeClass 生效
     */
    @Test
    public void emptyTest() {

    }

    /**
     * 创建自定义工作流
     */
    @Test
    public void deployProcess() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        //指定 workflow 文件
        deploymentBuilder.addClasspathResource("WorkflowDemo.bpmn");
        deploymentBuilder.deploy();
    }

    /**
     * 启动工作流
     */
    @Test
    public void startProcess() {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey("myProcess_1");
    }

    /**
     * 查询工作流Task信息，RU_TASK表
     */
    @Test
    public void findNodeTaskInfo() {
        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery();

        //assignee为task的执行人
        String assignee = "王思聪";
        List<Task> taskLists = taskQuery.taskAssignee(assignee).list();

        for (Task task : taskLists) {
            logger.info("taskId={}, assignee={}, category={}, createTime={}, description={}", task.getId(), task.getAssignee(), task.getCategory(), task.getCreateTime(), task.getDescription());
        }
    }

    /**
     * 启动某个task，该task结束后，流程进入第二个task
     */
    @Test
    public void startFirstTask() {
        //taskId从DB中ru_task中获取
        String taskId = "10004";
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(taskId);
    }

    /**
     * 执行完上面FirstTask后，执行第二个task
     */
    @Test
    public void startSecondTask() {
        String taskId = "12502";
        TaskService taskService = processEngine.getTaskService();
        taskService.complete(taskId);
    }


    /**
     * 查询工作流Process信息
     */
    @Test
    public void findProcessInfo() {

        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //按照主键Id查询
        //processDefinitionQuery.processDefinitionId("myProcess_1:1:4");

        //按照Key查询
        processDefinitionQuery.processDefinitionKey("myProcess_1");

        List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();

        //System.out.println("size="+processDefinitions.size());

        for (ProcessDefinition definition : processDefinitions) {
            logger.info("assignee={}, category={}, createTime={}, description={}", definition.getId(), definition.getCategory(), definition.getVersion(), definition.getDescription());
        }
    }

}
