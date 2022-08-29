#!/usr/bin/env Rscript

args = commandArgs(trailingOnly=TRUE)

# author: Annibale Panichella (2017)

# load required library
library(data.table)
library(effsize)
library(pracma)
#library(xtable)
library(PMCMRplus)

# weights for the score
w_i <- 1
w_b <- 2
w_m <- 4
w_faults <- 4

# parse the input parameters
if (length(args)<2) {
  stop("At least two argument must be supplied: (i) input file, and (ii) output folder.n", call.=FALSE)
} 

# create output directory
output_dir <- paste(args[2], "/output", sep="")
if (file.exists(output_dir) == FALSE)
  dir.create(output_dir, showWarnings = TRUE)

# read csv file
rawdata <- read.table(args[1], sep=",", header=TRUE);

# remove rows with "?"
for (row in 1:nrow(rawdata)){
  for (col in 1:ncol(rawdata)){
    if (rawdata[row,col]=="?")
      rawdata[row,col] = 0L
  }
}
rawdata<-rawdata[complete.cases(rawdata),]

# compute the score of each run
scores <- vector(mode = "list", length = nrow(rawdata))

for (index in 1:nrow(rawdata)) {
  point <- rawdata[index,]
  
  coverageScore <- 0
  coverageScore <- coverageScore + as.numeric(as.character(point$linesCoverageRatio))/100 * w_i 
  coverageScore <- coverageScore + as.numeric(as.character(point$conditionsCoverageRatio))/100 * w_b 
  coverageScore <- coverageScore + as.numeric(as.character(point$mutantsKillRatio))/100 * w_m 
  
  #if (as.numeric(as.character(point$failTests))>0)
  #  coverageScore <- coverageScore + w_faults
  
  # give a penalty when generationTime took too long
  if (as.numeric(as.character(point$generationTime)) == 0) {
    overtime_generation_penalty = 1.0;
  } else {
    timeBudgetMillis = as.numeric(as.character(point$timeBudget)) * 1000;
    generationTimeRatio = timeBudgetMillis / as.numeric(as.character(point$generationTime));
    overtime_generation_penalty = min(1, generationTimeRatio);
  }
  
  if (as.numeric(as.character(point$testcaseNumber)) == 0) {
    # no tests!
    coverageScore =  0.0;
  } else {
    if (as.numeric(as.character(point$uncompilableNumber)) == as.numeric(as.character(point$totalTestClasses))) {
      uncompilableFlakyPenalty = 2.0;
    } else {
      # assert testSuiteSize>0
      denominator <- as.numeric(as.character(point$testcaseNumber))
      denominator <- max(denominator, as.numeric(as.character(point$totalTestClasses)))
      flakyTestRatio = as.numeric(as.character(point$brokenTests))/denominator;

      # assert totalNumberOfTestClasses !=0
      uncompilableTestClassesRatio = as.numeric(as.character(point$uncompilableNumber)) / as.numeric(as.character(point$totalTestClasses));
      uncompilableFlakyPenalty = flakyTestRatio + uncompilableTestClassesRatio;
    }
    
    if (uncompilableFlakyPenalty>2.0)
      print("Error in the penalty function")
    
    coverageScore = (coverageScore * overtime_generation_penalty) - uncompilableFlakyPenalty;
    if (coverageScore < 0)
       coverageScore = 0;
  }
  
  scores[[index]] <- list(
    benchmark = point$benchmark,
    class = point$class, 
    run = as.numeric(point$run),
    timeBudget = as.numeric(point$timeBudget),
    tool = point$tool,
    score = coverageScore
  )
}
scores <- rbindlist(scores, fill = T)
write.csv(scores, file = paste(output_dir,"/detailed_score.csv", sep=""))

counter <- 1
average.scores <- vector(mode = "list")
for (project in unique(scores$benchmark)) {
  subset.project <- scores[scores$benchmark == project, ]
  #print(paste("project = ", project, "n.rows = ", nrow(subset.project)))
  
  for (actual.class in unique(subset.project$class)) {
    subset.class <- subset.project[subset.project$class == actual.class, ]
    #print(paste("classes = ", actual.class, "n.rows = ", nrow(subset.class)))
    
    for (actual.tool in unique(subset.class$tool)){
      subset.tool <- subset.class[subset.class$tool == actual.tool,]
      #print(paste("tool = ", actual.tool, "n.rows = ", nrow(points)))
      
      for (actual.budget in unique(subset.tool$timeBudget)){
        points <- subset.tool[subset.tool$timeBudget == actual.budget,]
        #print(paste("points = ", nrow(points)))
        
        average.scores[[counter]] <- list(
          benchmark = project,
          class = actual.class, 
          tool = actual.tool,
          budget = actual.budget,
          config = paste(project,"_",actual.class,"_",actual.budget, sep=""),
          score.mean = mean(points$score),
          score.sd = sd(points$score)
        )
        counter <- counter + 1
      }
    }
  }
}

#adjust the table for the missin lines
temp.table <- rbindlist(average.scores, fill = T)
configurations <- unique(temp.table$config)
for (thisTool in unique(temp.table$tool)){
  sub.table <- temp.table[ temp.table$tool == thisTool, ]
  for (configur in configurations) {
    temp <- sub.table[ sub.table$config == configur, ]
    if (nrow(temp)==0){
      #print(paste("Missing line for tool ",thisTool, "with configuration: ", configur, sep=""))
      
      # retrieve the configuration info from another tool
      line <- temp.table[ temp.table$config == configur, ]
      line <- line[1, ]
      
      # add missing line with zero score
      average.scores[[counter]] <- list(
         benchmark = line$benchmark,
         class = line$class, 
         tool = thisTool,
         budget = line$budget,
         config = configur,
         score.mean = 0,
         score.sd = 0
       )
       counter <- counter + 1
    }
  }
}

# write the table on file
average.scores <- rbindlist(average.scores, fill = T)
write.csv(average.scores, file = paste(output_dir,"/score_per_subject.csv", sep=""))
#latex.table <- xtable(average.scores)
#print(latex.table, type = "latex", file = paste(output_dir,"/score_per_subject.tex",sep=""), include.rownames=FALSE)


# apply the Friedman's test for statistical significance
res <- friedman.test(y = average.scores$score.mean, groups = factor(average.scores$tool), blocks = factor(average.scores$config))
print(res)
res = as.data.frame(do.call(rbind, res))
write.table(res, file = paste(output_dir,"/friedman_test.txt", sep=""))

# apply the post-hoc Kruskal's predecure 
res <- kwAllPairsConoverTest(x = average.scores$score.mean, g=as.factor(average.scores$tool))
print(res)
res = as.data.frame(res$p.value)
write.table(res, file = paste(output_dir,"/kruskal.txt", sep=""))

# compute final ranking
y = as.numeric(average.scores$score.mean)
groups = average.scores$tool
blocks = average.scores$config

k <- nlevels(factor(groups))
y <- matrix(unlist(split(y, blocks)), ncol = k, byrow = TRUE)
y <- y[complete.cases(y), ]
n <- nrow(y)
r <- t(apply(y, 1, rank))
r <- k - r + 1
tools <- unique(rawdata$tool)
colnames(r) = tools
r <- as.data.frame(r)
ranking <- as.data.frame(colMeans(r))
colnames(ranking) <- "Rank"
write.csv(ranking, file = paste(output_dir,"/final_ranking.txt", sep=""))


# compute average score of the tools for different time budgets
score.budget <- vector(mode = "list")
count <- 1
for (actual.tool in unique(average.scores$tool)){
  subset.tool <- average.scores[average.scores$tool == actual.tool,]
  
  for (actual.budget in unique(subset.tool$budget)){
    points <- subset.tool[subset.tool$budget == actual.budget,]
    
    score.budget[[counter]] <- list(
      tool = actual.tool,
      budget = actual.budget,
      score.sum = sum(points$score.mean),
      score.sd = sum(points$score.sd)
    )
    counter <- counter + 1
  }
}
score.budget <- rbindlist(score.budget, fill = T)
score.budget <- score.budget[ order(score.budget$budget), ]
write.csv(score.budget, file = paste(output_dir,"/average_score.csv", sep=""))
#latex.table <- xtable(score.budget)
#print(latex.table, type = "latex", file = paste(output_dir,"/average_score.tex", sep=""), include.rownames=FALSE)

# compute the final scores
final.score <- aggregate(score.sum ~ tool, data = score.budget, sum)
write.csv(final.score, file = paste(output_dir,"/final_score.csv", sep=""))
print(final.score)
