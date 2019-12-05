# Spring Boot学習 Injectionについて

## 依存性の注入（DI）

> 依存性の注入（いそんせいのちゅうにゅう、英: Dependency injection）とは、コンポーネント間の依存関係をプログラムのソースコードから排除するために、外部の設定ファイルなどでオブジェクトを注入できるようにするソフトウェアパターンである。英語の頭文字からDIと略される。
>
> [Wikipediaより](https://ja.wikipedia.org/wiki/%E4%BE%9D%E5%AD%98%E6%80%A7%E3%81%AE%E6%B3%A8%E5%85%A5)

コンポーネント間の依存関係をプログラムのソースコードから排除し、外部の設定ファイルなどで実行時に注入できるようにするソフトウェアパターンです。  
ここで言うコンポーネントとは、クラスと置き換えてもさしつかえありません。  

### 依存性

クラスAでクラスBをインスタンス化すると、クラスAはクラスBなしでは動作しません。  
この状態は、クラスAとクラスBが依存関係にある、と言えます。  
コードだと以下のような状態。

```
public class ClassA {
    public static void main(String... args) {
        ClassB classB = new ClassB();
        System.out.println(classB.execute());
    }
}

public class ClassB {
    public execute() {
        System.out.println("ClassB::execute()");
    }
}
```

### 注入

直接`new`するのではなく、設定などの外部ファイルに記載しておいて、実行時に指定クラスのインスタンスを割り当てることを言います。  
こういったことをやってくれる機能をDIコンテナと言います。  

SpringにはこのDIコンテナがあるので、設定をしておくと実行時にインスタンスを注入してくれます。  

### Spring BootのDI

SpringのDIコンテナにbeanとして登録するために、Springアノテーションを使います。  
以下のアノテーションは動作としてはいずれも同じなので、名前通りの使い分けで良いようです。  

|アノテーション|使い所|
|-------------|------|
| `@Service` | サービス層に配置 |
| `@Repository` | データ層に配置 |
| `@Controller` | コントローラ層に配置 |
| `@Component` | その他 |

DIコンテナに登録したクラスでは、注入が使えるようになります。  
注入はアノテーションで表現でき、以下の種類があります。

|アノテーション|パッケージ|
|-------------|----------|
| `@Autowired` | org.springframework.bean.factory |
| `@Inject` | javax.inject |
| `@Resource` | javax.annotation |

動作は変わらないので、ここでは`@Autowired`を使っていきます。
`@Autowired`で指定したインスタンスが注入されます。

### `@Autowired`の使い方

#### 準備

注入する方を作成します。  
入れ替えを行いたいので、`interface`を使ったものにします。

```puml
left to right direction

interface ICommand {
    void execute()
}
class FirstCommand 

FirstCommand --|> ICommand
SecondCommand --|> ICommand
```

#### コンストラクタインジェクション

コンストラクタに`@Autowired`をつけます。  
この方法はSpringチームが推奨している方法ですが、フィールドがfinalにできることや、コンストラクタに別のインスタンスが指定できるところがメリットです。  

```ConstructorAutowiredService.java
@Slf4j
@Service
public class ConstructorAutowiredService {

    private final IComponent component;

    @Autowired
    public ConstructorAutowiredService(IComponent secondComponent) {
        this.component = secondComponent;
    }

    @PostConstruct
    public void execute() {
        log.info("execute()");
        this.component.execute();
    }
}
```

コンストラクタの引数名（ここでは`secondComponent`）をクラス名のlower camelにすることで、自動的に`SecondComponent`を注入します。  
ここを`firstComponent`とすることで、`FirstComponent`が注入されます。

`ConstructorAutowiredService.java`では、わかりやすくうるために、コンストラクタを作ってそこに`@Autowired`をつけましたが、もっと簡潔に書くために、`final`のフィールドをコンストラクタ引数にとるアノテーション`@RequiredArgsConstructor`を使います。

```RequiredArgsConstructorService.java
@Slf4j
@RequiredArgsConstructor
@Service
public class RequiredArgsConstructorService {

    // @RequiredArgsConstructorを使った場合は@Autowiredは省略できる
    private final IComponent firstComponent;

    // もちろんConstructorも省略できる

    @PostConstruct
    public void execute() {
        log.info("execute()");
        this.firstComponent.execute();
    }
}
```

この書き方だと、`@Autowired`やコンストラクタが省略できます。

#### フィールドインジェクション

フィールドに`@Autowired`をつけます。  
わかりやすい方法です。  

```FirstComponentService.java
@Slf4j
@Service
public class FirstComponentService {

    @Autowired
    private IComponent firstComponent;
    
    @PostConstruct
    public void execute() {
        log.info("execute()");
        this.firstComponent.execute();
    }

}
```

```SecondComponentService.java
@Slf4j
@Service
public class SecondComponentService {

    @Autowired
    private IComponent secondComponent;
    
    @PostConstruct
    public void execute() {
        log.info("execute()");
        this.secondComponent.execute();
    }

}
```

注入するクラスの判別は名前になるところは、コンストラクタインジェクションと同様です。

#### セッターインジェクション

名前の通り、セッターに`@Autowired`をつけるものです。  
使い所が思いつかなかったので、サンプルはなしです。

### Configurationを使う

ここまで基本的な方法を見てきましたが、依存性の注入と言いながら、フィールド名がクラス名に紐付いていて使いにくかったです。  
そこで名前を指定して注入するインスタンスを分ける方法を示します。  

Configurationは、Beanを定義します。  *
つまり、DIコンテナに対して、**どのインスタンス**を**どの初期値**で注入するのかを設定します。  
Configurationは、
- 設定ファイル（xml）
- クラス
- アノテーション

で指定可能ですが、ここではアノテーションで作ります。

#### `@Configuration`クラスを作る

Configurationクラスには`@Configuration`アノテーションをつけます。  
メソッドには`@Bean`アノテーションをつけます。名前で判別可能にするために、引数を取ることができます。  
ここではシンプルに`new`しかしてませんが、書き方からわかるように、ここでインスタンスに値の設定をすることが可能です。

```App.java
@Configuration
public class App {

    @Bean("component1")
    public IComponent getComponent1() {
        return new FirstComponent();
    }

    @Bean("component2")
    public IComponent getComponent2() {
        return new SecondComponent();
    }
}
```

#### Configurationを使う

Configurationで設定したBeanを使って注入してみます。  
ここではコンストラクタインジェクションを例示します。

```UsinAppConfigService.java
@Slf4j
@Service
public class UsinAppConfigService {

    private final IComponent component;

    @Autowired
    public UsinAppConfigService(@Qualifier("component1") IComponent component) {
        this.component = component;
    }

    @PostConstruct
    public void execute() {
        log.info("service executed");
        this.component.execute();
    }
}
```

名前を指定してBeanを取得するためには、`@Qualifier`アノテーションを使います。
`@Qualifier`は`@Autowired`と組み合わせて使います。
コンストラクタの引数のとこにある`@Qualifier`で名前を指定しています。
実行すると、FirstComponentが実行されます。

## まとめ

ユニットテスト用にスタブにしたりできるので、開発には重宝しそうです。  
たとえば、RestTemplateはURLに問い合わせてJSONを持ち帰りますが、これをインジェクションして、固定のJSONを戻すことも可能ではないかと考えています。

ビジネス層では、ストラテジーパターンが必要な開発でも有用な機能です。  
用意されているDIコンテナを活用して、開発スピードをもっと上げていきたいですね。

## 参考資料

[DI(依存性注入)について](https://www.slideshare.net/yuiito94/di-56742600)
[@Component、@Service、@Repository、@Controllerの違いについて](https://qiita.com/KevinFQ/items/abc7369cb07eb4b9ae29)
[@Autowired、@Inject、@Resourceの違いについての検証](https://qiita.com/KevinFQ/items/20a6d53a5f93e28ab9ef)
