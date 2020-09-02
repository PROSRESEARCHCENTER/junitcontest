library(tidyverse)
library(ggplot2)

csvFile <- 'results.csv'
results <- read.csv(csvFile, stringsAsFactors = FALSE) %>%
  filter(timeBudget %in% c(60, 180)) %>%
  mutate(project = sub("([A-Z]+)\\-[0-9]+", "\\1", benchmark))

cat('Plotting results...')
df <- results %>%
  gather(Coverage, Value, linesCoverageRatio, conditionsCoverageRatio, mutantsKillRatio, mutantsCoverageRatio) %>%
  mutate(Coverage = recode(Coverage, linesCoverageRatio = "Line coverage", 
         conditionsCoverageRatio = "Branch coverage",
         mutantsKillRatio = "Mutation score", 
         mutantsCoverageRatio = "Covered mutants"),
         tool = recode(tool, evosuite = "E.", randoop = "R."),
         Configuration = paste0(tool, timeBudget/60)
)
df$Coverage <- factor(df$Coverage, levels = c("Line coverage", "Branch coverage", "Covered mutants", "Mutation score"))
df$Configuration <- factor(df$Configuration, levels = c("R.1", "E.1", "R.3", "E.3"))
p <- df %>%
  ggplot(aes(x = Configuration, y = Value, fill = tool)) +
  geom_boxplot() +
  stat_summary(fun.y=mean, geom="point", shape=23, color="black", fill="red") +
  scale_fill_brewer(palette="Set3") +
  theme(legend.position="none")+
  xlab(NULL) +
  ylab(NULL) +
  facet_grid(Coverage ~ project, scales = "free_y", margins= "project")
ggsave("plots/results.pdf", plot = p, width = 17, height = 35, units = "cm")
cat('DONE', '\n')

cat('Average coverages per tool and budget:', '\n')
df <- results %>%
  group_by(tool, timeBudget) %>%
  summarise(count = n(),
            mean_l_cov = mean(linesCoverageRatio),
            mean_b_cov = mean(conditionsCoverageRatio),
            mean_m_cov = mean(mutantsCoverageRatio),
            mean_m_s = mean(mutantsKillRatio))
print.data.frame(df)

cat('Average coverages per tool, budget, and project:', '\n')
df<- results %>%
  group_by(tool, timeBudget, project) %>%
  summarise(count = n(),
            mean_line_coverage = mean(linesCoverageRatio),
            mean_branch_coverage = mean(conditionsCoverageRatio),
            mean_mutant_coverage = mean(mutantsCoverageRatio),
            mean_mutation_score = mean(mutantsKillRatio))
print.data.frame(df)

cat('Plotting scores...')
csvFile <- 'score/detailed_score.csv'
scores <- read.csv(csvFile, stringsAsFactors = FALSE) %>%
  filter(timeBudget %in% c(60, 180)) %>%
  mutate(project = sub("([A-Z]+)\\-[0-9]+", "\\1", benchmark),
         tool = recode(tool, evosuite = "E.", randoop = "R."),
         Configuration = paste0(tool, timeBudget/60))
scores$Configuration <- factor(scores$Configuration, levels = c("R.1", "E.1", "R.3", "E.3"))
p <- scores %>%
  ggplot(aes(x = Configuration, y = score, fill = tool)) +
  geom_boxplot() +
  stat_summary(fun.y=mean, geom="point", shape=23, color="black", fill="red") +
  scale_fill_brewer(palette="Set3") +
  theme(legend.position="none")+
  xlab(NULL) +
  ylab(NULL) +
  facet_grid(. ~ project, margins= "project")
ggsave("plots/scores.pdf", plot = p, width = 17, height = 12, units = "cm")
cat('DONE', '\n')

