import { styled } from '@mui/material/styles';
import Box from '@mui/material/Box';
import MuiDrawer from '@mui/material/Drawer';
import MuiAppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import List from '@mui/material/List';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import InboxIcon from '@mui/icons-material/MoveToInbox';
import MailIcon from '@mui/icons-material/Mail';
import { useState } from 'react';

const drawerWidth = 240;

const openedMixin = (theme) => ({
  width: drawerWidth,
  transition: theme.transitions.create('width', {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.enteringScreen,
  }),
  overflowX: 'hidden',
});

const closedMixin = (theme) => ({
  transition: theme.transitions.create('width', {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  overflowX: 'hidden',
  width: `calc(${theme.spacing(7)} + 1px)`,
  [theme.breakpoints.up('sm')]: {
    width: `calc(${theme.spacing(8)} + 1px)`,
  },
});

const DrawerHeader = styled('div')(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'flex-end',
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
}));

const AppBar = styled(MuiAppBar, {
  shouldForwardProp: (prop) => prop !== 'open',
})(({ theme, open }) => ({
  zIndex: theme.zIndex.drawer + 1,
  boxShadow: "none",
  borderBottom: "1px solid #e0e0e0",
  backgroundColor: "transparent",
  backdropFilter: "blur(8px)",
  color: "black",
  transition: theme.transitions.create(['width', 'margin'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  ...(open && {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  }),
}));

const Drawer = styled(MuiDrawer, { shouldForwardProp: (prop) => prop !== 'open' })(
  ({ theme, open }) => ({
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: 'nowrap',
    boxSizing: 'border-box',
    ...(open && {
      ...openedMixin(theme),
      '& .MuiDrawer-paper': openedMixin(theme),
    }),
    ...(!open && {
      ...closedMixin(theme),
      '& .MuiDrawer-paper': closedMixin(theme),
    }),
  }),
);

export default function MiniDrawer() {
  const [open, setOpen] = useState(true);

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />

      <AppBar position="fixed" open={open}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            onClick={handleDrawerOpen}
            edge="start"
            sx={{
              marginRight: 5,
              ...(open && { display: 'none' }),
            }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div">
            Mini variant drawer
          </Typography>
        </Toolbar>
      </AppBar>

      <Drawer variant='permanent' open={open}>
        <DrawerHeader>
          <IconButton onClick={handleDrawerClose}>
            <ChevronLeftIcon />
          </IconButton>
        </DrawerHeader>
        <Divider />
        <List>
          {['Inbox', 'Starred', 'Send email', 'Drafts'].map((text, index) => (
            <ListItem key={text} disablePadding sx={{ display: 'block' }}>
              <ListItemButton
                sx={{
                  minHeight: 48,
                  justifyContent: open ? 'initial' : 'center',
                  px: 2.5,
                }}
              >
                <ListItemIcon
                  sx={{
                    minWidth: 0,
                    mr: open ? 3 : 'auto',
                    justifyContent: 'center',
                  }}
                >
                  {index % 2 === 0 ? <InboxIcon /> : <MailIcon />}
                </ListItemIcon>
                <ListItemText primary={text} sx={{ opacity: open ? 1 : 0 }} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
        <Divider />
        <List>
          {['All mail', 'Trash', 'Spam'].map((text, index) => (
            <ListItem key={text} disablePadding sx={{ display: 'block' }}>
              <ListItemButton
                sx={{
                  minHeight: 48,
                  justifyContent: open ? 'initial' : 'center',
                  px: 2.5,
                }}
              >
                <ListItemIcon
                  sx={{
                    minWidth: 0,
                    mr: open ? 3 : 'auto',
                    justifyContent: 'center',
                  }}
                >
                  {index % 2 === 0 ? <InboxIcon /> : <MailIcon />}
                </ListItemIcon>
                <ListItemText primary={text} sx={{ opacity: open ? 1 : 0 }} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Drawer>
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <DrawerHeader />
        <Typography paragraph>
          Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod
          tempor incididunt ut labore et dolore magna aliqua. Rhoncus dolor purus non
          enim praesent elementum facilisis leo vel. Risus at ultrices mi tempus
          imperdiet. Semper risus in hendrerit gravida rutrum quisque non tellus.
          Convallis convallis tellus id interdum velit laoreet id donec ultrices.
          Odio morbi quis commodo odio aenean sed adipiscing. Amet nisl suscipit
          adipiscing bibendum est ultricies integer quis. Cursus euismod quis viverra
          nibh cras. Metus vulputate eu scelerisque felis imperdiet proin fermentum
          leo. Mauris commodo quis imperdiet massa tincidunt. Cras tincidunt lobortis
          feugiat vivamus at augue. At augue eget arcu dictum varius duis at
          consectetur lorem. Velit sed ullamcorper morbi tincidunt. Lorem donec massa
          sapien faucibus et molestie ac.
        </Typography>
        <Typography paragraph>
          Consequat mauris nunc congue nisi vitae suscipit. Fringilla est ullamcorper
          eget nulla facilisi etiam dignissim diam. Pulvinar elementum integer enim
          neque volutpat ac tincidunt. Ornare suspendisse sed nisi lacus sed viverra
          tellus. Purus sit amet volutpat consequat mauris. Elementum eu facilisis
          sed odio morbi. Euismod lacinia at quis risus sed vulputate odio. Morbi
          tincidunt ornare massa eget egestas purus viverra accumsan in. In hendrerit
          gravida rutrum quisque non tellus orci ac. Pellentesque nec nam aliquam sem
          et tortor. Habitant morbi tristique senectus et. Adipiscing elit duis
          tristique sollicitudin nibh sit. Ornare aenean euismod elementum nisi quis
          eleifend. Commodo viverra maecenas accumsan lacus vel facilisis. Nulla
          posuere sollicitudin aliquam ultrices sagittis orci a.
          Lorem ipsum dolor sit amet consectetur adipisicing elit. Quo ipsam et eos, perferendis amet voluptates optio beatae deleniti hic ab quia quidem modi unde maxime, aliquid esse. Numquam libero explicabo sint quod dignissimos, dicta laboriosam labore harum, laborum, non et maiores aliquam veniam sapiente rerum? Quas quibusdam doloribus eveniet voluptatum libero sit rerum, inventore expedita quidem ex sed dolor laborum doloremque reprehenderit officiis ducimus pariatur qui facere placeat, nam corrupti nemo nesciunt consequatur delectus! Deleniti sunt a vitae culpa provident velit voluptate, facilis repudiandae beatae ea molestias maxime natus odio nostrum. Reiciendis omnis earum facere animi rem vero fuga accusamus, nam deleniti sed. Dolores temporibus voluptatem quas aspernatur, nesciunt dolore assumenda error vel natus, magni eveniet expedita quae distinctio quasi? Esse architecto maxime deleniti aperiam eum repellat. Sed quidem cupiditate aliquid velit amet beatae iusto repellat autem dolor, officia et quam consequuntur laboriosam ratione, minima vitae commodi, unde ab nostrum doloribus eos. Quo explicabo cupiditate esse error ullam dicta nam harum ipsam? Inventore id placeat alias odio quod amet iure unde illum, ratione ut animi aperiam non magnam veniam dolor fuga accusantium sunt illo totam quis? Nihil nulla voluptate fugit id ad quos nemo animi cumque! Deserunt, enim consequatur! Iste id qui error perspiciatis modi magni ab incidunt placeat, beatae dignissimos quod labore numquam rem voluptates sunt provident vitae, ea quaerat eligendi. Eveniet alias numquam debitis libero eius. Nobis expedita eligendi tenetur! Cum eligendi saepe aspernatur dignissimos corporis, consequuntur dolores, officiis, laboriosam nihil eveniet mollitia ipsa quis corrupti? Accusantium inventore ex eius perspiciatis fuga. Nesciunt nihil provident fugiat fugit deserunt aspernatur officia voluptate autem aliquam amet quia tenetur qui, necessitatibus iusto, perspiciatis laboriosam veritatis voluptatem quasi adipisci a nisi quis. Quis exercitationem iure pariatur beatae, reprehenderit architecto harum, dicta, quo vitae obcaecati cumque ab aliquid. Reiciendis maxime vero enim repellendus?
          Lorem ipsum dolor sit amet, consectetur adipisicing elit. Odit quibusdam repellat laborum eaque rerum magni modi, distinctio aperiam. Sit corrupti doloribus fuga voluptate quo, vel dicta animi odio asperiores incidunt culpa debitis neque sequi alias iste cum illum a libero deleniti quasi iure quas. Quam exercitationem itaque, soluta quo nobis temporibus facilis laboriosam totam qui quidem cumque fuga sunt laudantium eum sint aperiam rem voluptate nemo veritatis consequuntur perferendis culpa! Molestias dolorum nam quis ut nostrum! Dolores voluptatibus quas at, assumenda pariatur quasi maiores ex doloremque culpa repudiandae ipsam nihil fuga totam dicta expedita cupiditate provident aperiam quia quae id aliquid dolorum, saepe tenetur. At, a! At error, nulla iure quis perspiciatis deserunt quae doloremque illo suscipit cupiditate eligendi porro ut ab. Sunt numquam adipisci optio expedita hic ut, molestias est similique et, reiciendis debitis vero perspiciatis laborum doloribus sit praesentium accusantium consequuntur esse. Doloremque sequi et repellat quisquam facilis necessitatibus quae dolore quidem consequatur commodi odit hic voluptate numquam ea delectus pariatur voluptatem, fugit minus vero corrupti mollitia consequuntur nesciunt. Fugit, ut vel similique, inventore impedit voluptates laborum minima debitis vero quia itaque assumenda sunt aperiam labore quae nulla aut. Ad labore ullam quo? Aliquam aperiam nulla ab unde laudantium totam minima earum voluptate delectus quae repellendus, cumque saepe et, ex adipisci voluptatum quis ducimus similique quos! Quas nobis perferendis fugiat nam eaque blanditiis libero, saepe ea explicabo deleniti porro dolor cumque nesciunt minus quasi natus, fuga tempore accusamus sapiente quo unde dolorum perspiciatis vel. Harum, corporis consequuntur inventore nesciunt enim iste distinctio, eius esse nam officia id excepturi praesentium nulla nemo. Earum, quos! Aliquid rerum et sed aspernatur delectus perspiciatis blanditiis maxime asperiores sit, neque accusamus ratione quo dolor. Illum ab sint, possimus est similique quasi delectus cumque dolorem nam? Sapiente, iste est commodi, illo natus dicta, quasi animi eligendi officiis aliquid quae voluptatibus. Voluptatem cupiditate id eaque. Dolore cupiditate eius, odit tempora non at fugiat facere minus voluptatum, beatae tenetur incidunt eligendi alias deserunt distinctio nisi itaque pariatur officia reprehenderit magni! Minus reiciendis unde dolorum magni modi impedit rem maiores minima sapiente hic? Explicabo repudiandae non iure quo porro nobis, reiciendis tempore suscipit labore odit. Officia assumenda, culpa neque molestias voluptates ut provident obcaecati non fuga repellat quis esse aut error, nobis adipisci! Dolorem totam, minima perspiciatis obcaecati placeat odio, distinctio odit recusandae expedita illum sit quia quibusdam temporibus magnam dicta. Commodi eveniet similique suscipit tenetur voluptatum ab delectus neque impedit ex est numquam placeat aliquid, iusto voluptate provident maiores porro sapiente voluptates fuga, eos expedita tempora molestias, doloremque reprehenderit. Voluptate nisi magni quod dolores eaque? Optio delectus mollitia, aperiam hic dolorum obcaecati? Minima est, mollitia dolores tenetur quam a assumenda libero explicabo atque? Provident ut veritatis labore, sapiente vel molestiae quasi, nam itaque porro vitae dolore repellendus officia voluptas delectus quod maxime assumenda excepturi molestias fuga facere natus? A voluptatum eos debitis inventore in iure, ab eveniet repellendus sit minima quas cupiditate, repudiandae voluptatibus fugit! Cupiditate quidem reprehenderit obcaecati aspernatur pariatur dicta in sed, autem odit?
        </Typography>
      </Box>
    </Box>
  );
}
