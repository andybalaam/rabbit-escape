# Rabbit Escape

업데이트 내용

1. 다크 모드 구현
   사이드 메뉴에 다크 모드로 전환할 수 있는 토글 버튼을 추가하였습니다.
   버튼을 통해 다크 모드와 라이트 모드 간에 손쉽게 전환이 가능합니다.

사용자 설정 유지:
이전에 설정한 모드가 저장되어, 재접속 시 자동으로 적용됩니다.
토큰 추가 새로운 토큰 3종이 추가되었습니다:

2. 토큰 추가

1)  Jump
    - 기능: 토큰이 놓인 위치에서 x축으로 두 칸 점프할 수 있는 기능.
    - 특징: 점프 중간에 블록이 없어도 건너뜀.
2)  BreakBlock

    - 기능: 토큰을 블록 위에 놓으면 해당 블록을 삭제.
      토큰을 블록에 직접 두어도 동일한 동작 수행.
    - 특징: 특정 블록을 제거하는 데 사용 가능.

3)  Portal

    - 기능: 짝을 이루는 포탈을 생성하는 토큰.
      포탈에 접촉한 토끼를 다른 포탈이 위치한 곳으로 순간이동시킴.
    - 특징: 퍼즐과 동선 최적화에 활용 가능.

// TODO: 사용가이드에 맵 추가해주세요!
사용 가이드

다크 모드 활성화

화면 우측 상단의 사이드 메뉴에서 다크 모드 토글 버튼을 클릭하여 테마를 변경합니다.
토큰 사용법

Jump: 원하는 위치에 배치 후, x축으로 점프하도록 토큰을 활성화합니다.
BreakBlock: 삭제하려는 블록 위 또는 블록에 토큰을 놓아 삭제합니다.
Portal: 포탈 쌍을 생성하여 토끼의 순간이동 경로를 설정합니다.

업데이트 날짜: 2024-12-06

---

Rabbit Escape is a mobile and desktop game inspired by Lemmings
and Pingus in which you must rescue some rabbits from a hostile
environment by giving them special abilities.

Find out more at [Rabbit Escape](http://artificialworlds.net/rabbit-escape).

## Screenshots

Rabbit Escape works on Android:

![](https://raw.githubusercontent.com/andybalaam/rabbit-escape/master/doc/rabbitescape-android.png)

On PC (Linux, Windows, Mac) it runs via a Swing interface:

![](https://raw.githubusercontent.com/andybalaam/rabbit-escape/master/doc/minilevel/rabbitescape-minilevel.gif)

and, of course, text:

    ################################
    # r                            #
    #rf                            #
    #ff                            #
    #f            b                #
    #     Q     /\z           #    #
    #  #     /######\/#\     ###   #
    #\ #\   /###########\   b###  O#
    ################################

![](https://raw.githubusercontent.com/andybalaam/rabbit-escape/master/doc/minilevel/rabbitescape-minilevel-text.gif)

## Running

On PC:

    ./runrabbit swing   # Launch the UI
    ./runrabbit         # Launch text interface

## Make Levels

See the instructions and video at [Creating Levels](http://artificialworlds.net/rabbit-escape/create-levels.html).

## Developers

See [INSTALL.md](https://github.com/andybalaam/rabbit-escape/blob/master/INSTALL.md).

## Credits

Code, graphics, sound effects by [Andy Balaam](http://www.artificialworlds.net) and the [Rabbit Escape Developers](https://github.com/andybalaam/rabbit-escape/graphs/contributors).

Music samples by [tryad](http://tryad.org/).

---
