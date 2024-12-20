package org.astral.astral4xserver.dao;

import org.astral.astral4xserver.been.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;
@Component
public class UserFrpUpdate  implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    public static Queue<String> queue = new LinkedList<>();
    public void updateStream() {
        while (true) {
            if (queue.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                String cmd = queue.poll();
                long id = Long.parseLong(cmd.split(":")[0]);
                long stream = Long.parseLong(cmd.split(":")[1]);
                User user =this.userRepository.findById(id).get();
                long stream_next = user.getCountStream() - stream;
                this.userRepository.updateUserCountStream(stream_next, id);
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        updateStream();
    }
}
