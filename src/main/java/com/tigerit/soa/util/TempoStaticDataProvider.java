package com.tigerit.soa.util;

/**
 * Created by DIPU on 5/5/20
 */

import com.tigerit.soa.model.es.ProjectNote;
import com.tigerit.soa.model.es.TaskNote;
import com.tigerit.soa.request.es.ProjectNoteRequest;
import com.tigerit.soa.request.es.TaskNoteRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Mock data/repository
 * this class is generated only to provide static data for the service to
 * pacify work force. Will be used and modified concurrently.
 */
public class TempoStaticDataProvider {

    public static ProjectNote getDataForCreateProjectNote(ProjectNoteRequest noteRequest)
    {
         ProjectNote noteCreated=new ProjectNote();
         Util.copyProperty(noteRequest, noteCreated);

         noteCreated.setStatus("Your requset is echoed");
         return noteCreated;
    }

    public static List<ProjectNote> getProjectNoteList()
    {

        List<ProjectNote> projectNoteList=new ArrayList<>();
        for(int i=0;i<10;i++)
        {
            ProjectNote note= new ProjectNote();
            note.setId(String.valueOf(0+1));
            note.setNote("note-"+ String.valueOf(i+1));
            note.setDomainName("tigerit");
            note.setAccessLevel("reader");
            note.setCreatedBy("dipu");
            note.setCreateTime(new Date());
            //others
            note.setProjectId(String.valueOf(i+1));

            projectNoteList.add(note);
        }

        return projectNoteList;
    }

    public static TaskNote getDataForCreateTaskNote(TaskNoteRequest noteRequest)
    {
        TaskNote taskNote= new TaskNote();
        Util.copyProperty(noteRequest, taskNote);

        taskNote.setStatus("Your requset is echoed");
        return taskNote;
    }

    public static List<TaskNote> getTaskNoteList()
    {

        List<TaskNote> taskNoteList=new ArrayList<>();
        for(int i=0;i<10;i++)
        {
            TaskNote note= new TaskNote();
            note.setId(String.valueOf(0+1));
            note.setNote("note-"+ String.valueOf(i+1));
            note.setDomainName("tigerit");
            note.setAccessLevel("reader");
            note.setCreatedBy("dipu");
            note.setCreateTime(new Date());
            //others
            note.setTaskId(String.valueOf(i+1));

            taskNoteList.add(note);
        }

        return taskNoteList;
    }

}
