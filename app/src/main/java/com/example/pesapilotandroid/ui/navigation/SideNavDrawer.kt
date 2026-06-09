package com.example.pesapilotandroid.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.pesapilotandroid.navigation.NavRoute
import com.example.pesapilotandroid.ui.screens.auth.AuthViewModel
import com.example.pesapilotandroid.ui.theme.*

// ─── Data model ──────────────────────────────────────────────────────────────

data class DrawerMenuItem(
    val label: String,
    val icon: ImageVector,
    val route: NavRoute
)

data class DrawerSection(
    val title: String,
    val items: List<DrawerMenuItem>
)

private fun buildDrawerSections(isAdmin: Boolean): List<DrawerSection> = buildList {
    add(DrawerSection(
        title = "Overview",
        items = listOf(
            DrawerMenuItem("Dashboard", Icons.Filled.Dashboard, NavRoute.Dashboard)
        )
    ))
    add(DrawerSection(
        title = "Start & Grow",
        items = listOf(
            DrawerMenuItem("Business Discovery", Icons.Filled.Explore,      NavRoute.BusinessDiscovery),
            DrawerMenuItem("My Businesses",      Icons.Filled.Business,     NavRoute.MyBusinesses),
            DrawerMenuItem("Roadmaps",           Icons.Filled.FormatListBulleted, NavRoute.Roadmaps),
            DrawerMenuItem("Business Plans",     Icons.Filled.MenuBook,     NavRoute.BusinessPlans)
        )
    ))
    add(DrawerSection(
        title = "Business Finance",
        items = listOf(
            DrawerMenuItem("Accounting",          Icons.Filled.ShowChart,      NavRoute.Accounting),
            DrawerMenuItem("Reports",             Icons.Filled.BarChart,       NavRoute.Reports),
            DrawerMenuItem("Bank Accounts",       Icons.Filled.AccountBalance, NavRoute.BankAccounts),
            DrawerMenuItem("Vendors & Customers", Icons.Filled.People,         NavRoute.VendorsCustomers)
        )
    ))
    add(DrawerSection(
        title = "Personal Finance",
        items = listOf(
            DrawerMenuItem("Income & Expenses", Icons.Filled.AccountBalanceWallet, NavRoute.IncomeExpenses),
            DrawerMenuItem("Savings Goals",     Icons.Filled.Savings,              NavRoute.SavingsGoals),
            DrawerMenuItem("Wallet Planner",    Icons.Filled.Wallet,               NavRoute.WalletPlanner)
        )
    ))
    add(DrawerSection(
        title = "Discover",
        items = listOf(
            DrawerMenuItem("Lenders", Icons.Filled.AccountBalance, NavRoute.LenderDirectory)
        )
    ))
    add(DrawerSection(
        title = "Account",
        items = buildList {
            add(DrawerMenuItem("Profile", Icons.Filled.Person, NavRoute.Profile))
            if (isAdmin) {
                add(DrawerMenuItem("Admin", Icons.Filled.AdminPanelSettings, NavRoute.AdminOverview))
            }
        }
    ))
}

// ─── Public entry point ───────────────────────────────────────────────────────

@Composable
fun PesaPilotSideDrawer(
    currentDestination: NavDestination?,
    authViewModel: AuthViewModel,
    onNavigate: (NavRoute) -> Unit,
    onSignOut: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val userEmail = ""          // replace with actual email from profile if available
    val isAdmin   = false       // replace with actual role check

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SidebarBackground)
            .systemBarsPadding()
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        DrawerHeader()

        HorizontalDivider(color = SidebarBorder, thickness = 1.dp)

        // ── Menu ─────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            buildDrawerSections(isAdmin).forEach { section ->
                DrawerSectionLabel(section.title)
                section.items.forEach { item ->
                    val isSelected = currentDestination?.hasRoute(item.route::class) == true
                    DrawerMenuRow(
                        item     = item,
                        selected = isSelected,
                        onClick  = { onNavigate(item.route) }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        HorizontalDivider(color = SidebarBorder, thickness = 1.dp)

        // ── Footer ───────────────────────────────────────────────────────────
        DrawerFooter(
            userEmail = userEmail,
            onSignOut = onSignOut
        )
    }
}

// ─── Header ──────────────────────────────────────────────────────────────────

@Composable
private fun DrawerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SidebarLogoBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint               = Color.White,
                modifier           = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text       = "Pilot-Pesa",
            color      = Color.White,
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp
        )
    }
}

// ─── Section label ───────────────────────────────────────────────────────────

@Composable
private fun DrawerSectionLabel(title: String) {
    Text(
        text       = title.uppercase(),
        color      = SidebarIconDefault,
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 11.sp,
        modifier   = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
    )
}

// ─── Menu row ────────────────────────────────────────────────────────────────

@Composable
private fun DrawerMenuRow(
    item: DrawerMenuItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor   = if (selected) SidebarActiveBg else Color.Transparent
    val iconColor = if (selected) SidebarIconActive else SidebarIconDefault
    val textColor = if (selected) SidebarActiveText else SidebarText
    val fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = item.icon,
            contentDescription = item.label,
            tint               = iconColor,
            modifier           = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text       = item.label,
            color      = textColor,
            fontFamily = FigtreeFamily,
            fontWeight = fontWeight,
            fontSize   = 14.sp
        )
    }
}

// ─── Footer ──────────────────────────────────────────────────────────────────

@Composable
private fun DrawerFooter(
    userEmail: String,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (userEmail.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SidebarActiveBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = userEmail.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color      = Color.White,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = userEmail,
                    color      = SidebarIconDefault,
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize   = 12.sp,
                    maxLines   = 1
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onSignOut)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = Icons.Filled.Logout,
                contentDescription = "Sign out",
                tint               = SidebarIconDefault,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text       = "Sign out",
                color      = SidebarIconDefault,
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.Medium,
                fontSize   = 14.sp
            )
        }
    }
}
