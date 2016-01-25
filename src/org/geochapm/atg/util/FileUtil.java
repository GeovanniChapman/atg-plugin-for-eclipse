package org.geochapm.atg.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 
 * @author geovanni.chapman
 *
 */
public class FileUtil {

	/**
	 * Create parents directories by String directory path
	 * 
	 * @param path String directory path
	 * @return true if create directories or false if not.
	 */
	public static boolean createDirectories(String path) {
		File file = new File(path);
		if (file.mkdirs()) {
			CommonUtil.printInfoMessageConsoleStream(String.format("Directory created : %s", path));
			return true;
		}
		return false;
	}
	
	/**
	 * Find directories that matching with directoryName parameter in the directory defined by parentDirectory parameter.
	 * 
	 * @param parentDirectory base directory
	 * @param directoryName name of directories to search
	 * @return Set of String with absolutes paths that matching whit directoryName parameter
	 */
	public static Set<String> findDirectories(String parentDirectory, String directoryName) {
		Set<String> foundDirectories = new HashSet<>();
		class InnerFindDirectories {
			private void findDirectories(File parentDirectory, Set<String> foundDirectories, String directoryName) {
		        File[] files = parentDirectory.listFiles();
		        for (File file : files) {
		            if (file.isDirectory()) {
			            if (file.getName().equals(directoryName)) {
			            	foundDirectories.add(file.getPath());
			            } else {
			            	findDirectories(file, foundDirectories, directoryName);
			            }
		            }
		        }
		    }
		}
		new InnerFindDirectories().findDirectories(new File(parentDirectory), foundDirectories, directoryName);
		return foundDirectories;
	}
	
	/**
	 * Validate if the path exists.
	 * 
	 * @param path to validate
	 * @return true boolean indicating if the path exists 
	 */
	public static boolean existsPath(String path) {
		return new File(path).exists();
	}
	
	/**
	 * Find files in parallel that matching with fileNameToSearch parameter in the directory defined by parentDirectory parameter.
	 * 
	 * @param parentDirectory base directory
	 * @param fileNameToSearch name of files to search
	 * @return Set of File with absolutes paths that matching whit directoryName parameter
	 */
	public static Set<File> findFiles(String parentDirectory, String fileNameToSearch) {

		ForkJoinPool pool = new ForkJoinPool();
		//Create three FolderProcessor tasks. Initialize each one with a different folder path.
		FolderProcessor mf = new FolderProcessor(parentDirectory, fileNameToSearch);
		pool.execute(mf);
		
		/*
		 Let us to see the execution in parallel
		do
		{
			 System.out.printf("******************************************\n");
			 System.out.printf("Main: Parallelism: %d\n", pool.getParallelism());
			 System.out.printf("Main: Active Threads: %d\n", pool.getActiveThreadCount());
			 System.out.printf("Main: Task Count: %d\n", pool.getQueuedTaskCount());
			 System.out.printf("Main: Steal Count: %d\n", pool.getStealCount());
			 System.out.printf("******************************************\n");
			 try {
			    TimeUnit.SECONDS.sleep(1);
			 } catch (InterruptedException e) {
			    e.printStackTrace();
			 }
		} while (!mf.isDone());
		*/
		
		pool.shutdown();
		Set<File> files;
		files = mf.join();
		//System.out.printf("System: %d files found.\n", files.size());
		return files;   
	}
	public static boolean deleteFile(String path) {
		File file = new File(path);
		return file.delete();
	}
}

class FolderProcessor extends RecursiveTask<Set<File>> {
  private static final long serialVersionUID = 1L;
  //This attribute will store the full path of the folder this task is going to process.
  private final String      path;
  //This attribute will store the name of the extension of the files this task is going to look for.
  private final String      fileNameToSearch;

  //Implement the constructor of the class to initialize its attributes
  public FolderProcessor(String path, String fileNameToSearch)
  {
     this.path = path;
     this.fileNameToSearch = fileNameToSearch;
  }

  //Implement the compute() method. As you parameterized the RecursiveTask class with the List<String> type,
  //this method has to return an object of that type.
  @Override
  protected Set<File> compute()
  {
     //List to store the names of the files stored in the folder.
     Set<File> list = new HashSet<>();
     //FolderProcessor tasks to store the subtasks that are going to process the subfolders stored in the folder
     List<FolderProcessor> tasks = new ArrayList<FolderProcessor>();
     //Get the content of the folder.
     File file = new File(path);
     File content[] = file.listFiles();
     //For each element in the folder, if there is a subfolder, create a new FolderProcessor object
     //and execute it asynchronously using the fork() method.
     if (content != null)
     {
        for (int i = 0; i < content.length; i++)
        {
           if (content[i].isDirectory())
           {
              FolderProcessor task = new FolderProcessor(content[i].getAbsolutePath(), fileNameToSearch);
              task.fork();
              tasks.add(task);
           }
           //Otherwise, compare the extension of the file with the extension you are looking for using the checkFile() method
           //and, if they are equal, store the full path of the file in the list of strings declared earlier.
           else
           {
              if (checkFile(content[i].getName()))
              {
                 list.add(content[i]);
              }
           }
        }
     }
     //If the list of the FolderProcessor subtasks has more than 50 elements,
     //write a message to the console to indicate this circumstance.
     if (tasks.size() > 50)
     {
        System.out.printf("%s: %d tasks ran.\n", file.getAbsolutePath(), tasks.size());
     }
     //add to the list of files the results returned by the subtasks launched by this task.
     addResultsFromTasks(list, tasks);
     //Return the list of strings
     return list;
  }

  //For each task stored in the list of tasks, call the join() method that will wait for its finalization and then will return the result of the task.
  //Add that result to the list of strings using the addAll() method.
  private void addResultsFromTasks(Set<File> list, List<FolderProcessor> tasks)
  {
     for (FolderProcessor item : tasks)
     {
        list.addAll(item.join());
     }
  }

  //This method compares if the name of a file passed as a parameter ends with the extension you are looking for.
  private boolean checkFile(String name)
  {
     return name.equals(fileNameToSearch);
  }
}
