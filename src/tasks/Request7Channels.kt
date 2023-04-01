package tasks

import contributors.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    coroutineScope {
        val userChannel = Channel<List<User>>()
        var allUsers = emptyList<User>()

        val repos = service.getOrgRepos(req.org).also {
            logRepos(req, it)
        }.bodyList()

        repos.forEach { repo ->
            launch {
                val users = service.getRepoContributors(req.org, repo.name).also {
                    logUsers(repo, it)
                }.bodyList()
                userChannel.send(users)
            }
        }

        repeat(repos.size){ index ->
            val users = userChannel.receive()
            allUsers = (allUsers + users).aggregate()
            updateResults(allUsers, index == repos.lastIndex)
        }
    }
}
