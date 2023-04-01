package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service.getOrgRepos(req.org).also {
        logRepos(req, it)
    }.bodyList()

    var users = emptyList<User>()
    repos.forEachIndexed { index, repo ->
        val user = service.getRepoContributors(req.org, repo.name).also {
            logUsers(repo, it)
        }.bodyList()

        users = (users + user).aggregate()
        updateResults(users, index == repos.lastIndex)
    }
}
