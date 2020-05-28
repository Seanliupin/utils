# 使用方法
## 简要定义返回结果

```java
@Data
@AllArgsConstructor
@Builder
public class SimpleResult {
    public static final Option<SimpleResult> created = Option.of(new SimpleResult(200, "create ok"));
    public static final Option<SimpleResult> deleted = Option.of(new SimpleResult(200, "delete ok"));
    public static final Option<SimpleResult> updated = Option.of(new SimpleResult(200, "update ok"));

    private int code;
    private String msg;

    public static Option<SimpleResult> ok(String msg) {
        return Option.of(new SimpleResult(200, msg));
    }

    public static Option<SimpleResult> error(int code, String msg) {
        return Option.of(new SimpleResult(code, msg));
    }
}
```

## 在Service中定义业务逻辑。
```java
@Service
public class NoteService extends BaseService<Note> {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    protected BaseRepo<Note, Long> getRepo() {
        return noteRepository;
    }

    public Cat<List<Note>> findByUser(@Param("id") Long userID, Pageable pageable) {
        try {
            return Cat.of(noteRepository.findByUser(userID, pageable));
        } catch (Exception e) {
            return Cat.empty(e);
        }
    }

    public Cat<Note> findByUserAndID(Long id, Long userID) {
        try {
            Note note = noteRepository.findByUserAndID(id, userID);
            if (note == null) {
                return Cat.empty(itemNotExistException);
            } else {
                return Cat.of(note);
            }
        } catch (Exception e) {
            return Cat.empty(e);
        }
    }

    @Override
    public Cat<Boolean> deleteById(Long aLong) {
        try {
            noteRepository.deleteById(aLong);
            return Cat.of(true);
        } catch (Exception e) {
            return Cat.empty(new DbException(String.format("error when delete : %s", e.getMessage())));
        }
    }
}
```


## 在Controller中调用Service
```java
@RestController
@RequestMapping("note")
@Slf4j
public class NoteController {

    @Autowired
    private NoteService noteService;

    /**
     * 用户只能删除自己的Note
     *
     * @param user
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Mono<SimpleResult> deleteNote(@CurrentUser User user, @PathVariable long id) {
        return noteService.findByUserAndID(user.getId(), id)
                //如果根据当前用户和noteID能够找到note，则删除该Note
                .someFlatMap(note -> noteService.deleteById(id))
                //如果删除成功，则生成简要的信息返回给前端
                .someMap(ok -> SimpleResult.deleted)
                .getMono();
    }
}
```
