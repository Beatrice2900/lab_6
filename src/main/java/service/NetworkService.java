package service;

import domain.entities.Friendship;
import domain.entities.Tuple;
import domain.entities.User;
import repository.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static service.StreamUtils.stream;

/**
 * Service that answers questions about the network as a whole.
 * The network is seen as an undirected graph: users are nodes and friendships are edges.
 */
public class NetworkService {
    private Repository<Long, User> userRepo;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepo;

    /**
     * Creates a network service.
     *
     * @param userRepo       the repository of users
     * @param friendshipRepo the repository of friendships
     */
    public NetworkService(Repository<Long, User> userRepo, Repository<Tuple<Long, Long>, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    private Map<Long, List<Long>> buildGraph() {
        Map<Long, List<Long>> graph = stream(userRepo.findAll())
                .collect(Collectors.toMap(User::getId, u -> new ArrayList<Long>()));
        stream(friendshipRepo.findAll()).forEach(f -> {
            graph.get(f.getUserId1()).add(f.getUserId2());
            graph.get(f.getUserId2()).add(f.getUserId1());
        });
        return graph;
    }

    private void collectComponent(Long node, Map<Long, List<Long>> graph,
                                  Set<Long> visited, List<Long> component) {
        visited.add(node);
        component.add(node);
        graph.get(node).stream()
                .filter(n -> !visited.contains(n))
                .forEach(n -> collectComponent(n, graph, visited, component));
    }

    private List<List<Long>> components() {
        Map<Long, List<Long>> graph = buildGraph();
        Set<Long> visited = new HashSet<>();
        return graph.keySet().stream()
                .filter(node -> !visited.contains(node))
                .map(node -> {
                    List<Long> component = new ArrayList<>();
                    collectComponent(node, graph, visited, component);
                    return component;
                })
                .collect(Collectors.toList());
    }

    private int longestPath(Long node, Map<Long, List<Long>> graph, Set<Long> onPath) {
        onPath.add(node);
        int best = graph.get(node).stream()
                .filter(n -> !onPath.contains(n))
                .mapToInt(n -> 1 + longestPath(n, graph, onPath))
                .max()
                .orElse(0);
        onPath.remove(node);
        return best;
    }

    /**
     * Counts the communities of the network, i.e. its connected components.
     * A user without friends is a community by itself.
     *
     * @return the number of communities
     */
    public int numberOfCommunities() {
        return components().size();
    }

    /**
     * Finds the most sociable community, i.e. the connected component
     * that contains the longest simple path (a path that does not repeat users).
     *
     * @return the ids of the users in the most sociable community,
     *         or an empty list if the network has no users
     */
    public List<Long> mostSociableCommunity() {
        Map<Long, List<Long>> graph = buildGraph();
        return components().stream()
                .max(Comparator.comparingInt((List<Long> c) -> c.stream()
                        .mapToInt(start -> longestPath(start, graph, new HashSet<>()))
                        .max()
                        .orElse(0)))
                .orElse(new ArrayList<>());
    }
}