FROM atlassian/jira-software:9.4.0
USER root

# Add agent file
COPY jira-crack/atlassian-agent.jar /opt/atlassian/jira/

# Add agent to env
RUN echo 'export CATALINA_OPTS="-javaagent:/opt/atlassian/jira/atlassian-agent.jar ${CATALINA_OPTS}"' >> /opt/atlassian/jira/bin/setenv.sh