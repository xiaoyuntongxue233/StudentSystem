package chain;
import job.User;
public class UserNode {
    public User data;    
    public UserNode next;       

    public UserNode(User data) {
        this.data = data;
        this.next = null;
        
    }

}
