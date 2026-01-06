package _blog.demo.service;

import _blog.demo.model.Post;
import _blog.demo.model.Subscription;
import _blog.demo.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class FeedService {
    private PostRepository postRepo;
    private SubscriptionService subscriptionService;

    public Page<Post> getPersonalizedFeed(Long userId, int page, int size) {
        List<Subscription> followList = subscriptionService.getFollowing(userId);
        if (followList.isEmpty()) {
            return Page.empty(PageRequest.of(page, size));
        }
        List<Long> folllowingsIDS = followList.stream().map(Subscription::getFollowingId).collect(Collectors.toList());
        return postRepo.findPostsByFollowingUsers(
                folllowingsIDS, PageRequest.of(page, size,
                        Sort.by(Sort.Direction.DESC, "createdAt")));
    }






    //  public Page<Post> getPersonalizedFeedOptimized(Long userId, int page, int size) {
    //     return postRepo.findFeedForUser(userId, PageRequest.of(page, size));
    // }




    // that in case the MF doesn't folllow anyone; 
    public Page<Post> getExploreFeed(int page, int size) {
        return postRepo.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }


 

}
